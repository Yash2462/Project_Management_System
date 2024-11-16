package com.projectmanagementsystembackend.controller;

import com.projectmanagementsystembackend.config.JwtProvider;
import com.projectmanagementsystembackend.model.User;
import com.projectmanagementsystembackend.repository.UserRepository;
import com.projectmanagementsystembackend.request.LoginRequest;
import com.projectmanagementsystembackend.service.CustomUserDetailsImpl;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest){

        String username = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        Authentication authentication = authenticate(username,password);

        Authentication authentication1 = new UsernamePasswordAuthenticationToken(username,password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = JwtProvider.generateToken(authentication);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setMessage("User Logged in successfully");
        authResponse.setStatus(200);

        return new ResponseEntity<>(authResponse,HttpStatus.OK);
    }

    private Authentication authenticate(String username , String password) {

        UserDetails userDetails  = customUserDetails.loadUserByUsername(username);

        if (userDetails == null){
            throw new BadCredentialsException("invalid username");
        }
        if (!passwordEncoder.matches(password,userDetails.getPassword())){
            throw new BadCredentialsException("invalid password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
    }
}
