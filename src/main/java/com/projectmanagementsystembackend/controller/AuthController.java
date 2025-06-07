package com.projectmanagementsystembackend.controller;

import com.projectmanagementsystembackend.config.JwtProvider;
import com.projectmanagementsystembackend.model.User;
import com.projectmanagementsystembackend.repository.UserRepository;
import com.projectmanagementsystembackend.request.LoginRequest;
import com.projectmanagementsystembackend.service.CustomUserDetailsImpl;
import com.projectmanagementsystembackend.service.EmailService;
import com.projectmanagementsystembackend.service.OtpService;
import com.projectmanagementsystembackend.service.SubscriptionService;
import com.projectmanagementsystembackend.vo.AuthResponse;
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
        authResponse.setJwt(jwt);

        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping("/send-otp/{email}")
    public ResponseEntity<Object> sendOtp(@PathVariable(value = "email") String email) {
        AuthResponse authResponse = new AuthResponse();
        try {
            String otp = otpService.generateOtp(email);
            emailService.sendOtp(email, otp);
            authResponse.setMessage("OTP sent successfully");
            authResponse.setStatus(200);
            return new ResponseEntity<>(authResponse, HttpStatus.OK);
        } catch (Exception e) {
            authResponse.setMessage("Failed to send OTP: " + e.getMessage());
            authResponse.setStatus(500);
            return new ResponseEntity<>(authResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = new AuthResponse();
        try {
            String username = loginRequest.getEmail();
            String password = loginRequest.getPassword();
            String otp = loginRequest.getOtp();
            UserDetails userDetails = customUserDetails.loadUserByUsername(username);
            if (userDetails == null) {
                authResponse.setMessage("User not found");
                authResponse.setStatus(404);
                return new ResponseEntity<>(authResponse, HttpStatus.NOT_FOUND);
            }
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
            authResponse.setJwt(jwt);
            authResponse.setMessage("User Logged in successfully");
            authResponse.setStatus(200);

            return new ResponseEntity<>(authResponse, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            authResponse.setMessage("Invalid credentials: " + e.getMessage());
            authResponse.setStatus(401);
            return new ResponseEntity<>(authResponse, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            authResponse.setMessage("Login failed: " + e.getMessage());
            authResponse.setStatus(500);
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
