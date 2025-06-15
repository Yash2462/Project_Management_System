package com.projectmanagementsystembackend.controller;

import com.projectmanagementsystembackend.config.JwtProvider;
import com.projectmanagementsystembackend.model.User;
import com.projectmanagementsystembackend.ratelimiter.EmailRateLimit;
import com.projectmanagementsystembackend.ratelimiter.RateLimit;
import com.projectmanagementsystembackend.repository.UserRepository;
import com.projectmanagementsystembackend.request.LoginRequest;
import com.projectmanagementsystembackend.service.CustomUserDetailsImpl;
import com.projectmanagementsystembackend.service.EmailService;
import com.projectmanagementsystembackend.service.OtpService;
import com.projectmanagementsystembackend.service.SubscriptionService;
import com.projectmanagementsystembackend.vo.AuthResponse;
import com.projectmanagementsystembackend.vo.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private CustomUserDetailsImpl customUserDetails;
    @Autowired
    private OtpService otpService;
    @Autowired
    private EmailService emailService;

    // signup api
    @PostMapping("/signup")
    public ResponseEntity<Object> createUserHandler(@RequestBody User user) throws Exception {
        try {
            User isExist = userRepository.findByEmail(user.getEmail());

            if (isExist != null){
                throw new Exception("email already exist with another Account");
            }
            User createUser = new User();
            createUser.setEmail(user.getEmail());
            createUser.setPassword(passwordEncoder.encode(user.getPassword()));
            createUser.setFullName(user.getFullName());

            User savedUser = userRepository.save(createUser);

            subscriptionService.createSubscription(savedUser);

            Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(),user.getPassword());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = JwtProvider.generateToken(authentication);
            AuthResponse authResponse = new AuthResponse();
            authResponse.setMessage("User Saved Successfully");
            authResponse.setStatus(201);
            authResponse.setToken(jwt);

            return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseMessage.getServerError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @RateLimit(permits = 5,durationSeconds = 60)
    @EmailRateLimit(permits = 5, durationSeconds = 60)
    @PostMapping("/send-otp")
    public ResponseEntity<Object> sendOtp(@RequestParam(value = "email") String email) {
        AuthResponse authResponse = new AuthResponse();
        try {
            log.info("otp request received for email: {}", email);
            String otp = otpService.generateOtp(email);
            //emailService.sendOtp(email, otp);
            return new ResponseEntity<>(ResponseMessage.Generic.OTP_SENT, HttpStatus.OK);
        } catch (Exception e) {
            authResponse.setMessage("Failed to send OTP: " + e.getMessage());
            authResponse.setStatus(500);
            return new ResponseEntity<>(authResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RateLimit(permits = 10 , durationSeconds = 60)
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = new AuthResponse();
        try {
            String username = loginRequest.getEmail();
            String password = loginRequest.getPassword();
            String otp = loginRequest.getOtp();
            User user = userRepository.findByEmail(username);
            if (user == null) {
                authResponse.setMessage("User not found");
                authResponse.setStatus(404);
                return new ResponseEntity<>(authResponse, HttpStatus.NOT_FOUND);
            }
            //find userDetails if user exists
            UserDetails userDetails = customUserDetails.loadUserByUsername(username);
            Authentication authentication;
            // Check if OTP is provided
            if (otp != null && !otp.isEmpty()) {
                if (!otpService.validateOtp(username, otp)) {
                    // If OTP is not valid
                    authResponse.setMessage("Invalid OTP");
                    authResponse.setStatus(400);
                    return new ResponseEntity<>(authResponse, HttpStatus.BAD_REQUEST);
                }
                authentication = new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());
            } else {
//            authentication = authenticate(username, password);
                if (passwordEncoder.matches(password, userDetails.getPassword())) {
                    authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                } else {
                    throw new BadCredentialsException("Invalid password");
                }
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = JwtProvider.generateToken(authentication);
            authResponse.setToken(jwt);
            authResponse.setMessage("User Logged in successfully");
            authResponse.setStatus(200);
            log.info("User logged in successfully: {}", username);
            return new ResponseEntity<>(authResponse, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            authResponse.setMessage("Invalid credentials: " + e.getMessage());
            authResponse.setStatus(401);
            log.info("Login failed for user: {}. Reason: {}", loginRequest.getEmail(), e.getMessage());
            return new ResponseEntity<>(authResponse, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            authResponse.setMessage("Login failed: " + e.getMessage());
            authResponse.setStatus(500);
            log.error("Login failed for user: {}. Reason: {}", loginRequest.getEmail(), e.getMessage());
            return new ResponseEntity<>(authResponse, HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

//    private Authentication authenticate(String username , String password) {
//
//        UserDetails userDetails  = customUserDetails.loadUserByUsername(username);
//
//        if (userDetails == null){
//            throw new BadCredentialsException("invalid username");
//        }
//        if (!passwordEncoder.matches(password,userDetails.getPassword())){
//            throw new BadCredentialsException("invalid password");
//        }
//
//        return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
//    }
}
