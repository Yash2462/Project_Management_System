package com.projectmanagementsystembackend.controller;

import com.projectmanagementsystembackend.model.User;
import com.projectmanagementsystembackend.repository.UserRepository;
import com.projectmanagementsystembackend.service.UserService;
import com.projectmanagementsystembackend.vo.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public ResponseEntity<Object> getUserProfile(@RequestHeader("Authorization") String jwt) throws Exception{
        User user = userService.findUserProfileByJwt(jwt);
        ResponseMessage responseMessage = new ResponseMessage();

        if (user == null){
            responseMessage.setMessage("not found user with given details");
            responseMessage.setStatus(404);
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }

        responseMessage.setMessage("User Profile found successfully");
        responseMessage.setStatus(200);
        responseMessage.setData(user);

        return new ResponseEntity<>(responseMessage,HttpStatus.OK);
    }
    
    @GetMapping("/profiles")
    public ResponseEntity<Object> getUsers(){

        List<User> users = userRepository.findAll();
        ResponseMessage responseMessage = new ResponseMessage();
        if (users.isEmpty()){
            responseMessage.setMessage("Not found any users");
            responseMessage.setStatus(404);
            
            return new ResponseEntity<>(responseMessage,HttpStatus.NOT_FOUND);
        }
        
        responseMessage.setMessage("Users list found successfully");
        responseMessage.setStatus(200);
        responseMessage.setData(users);
        
        return new ResponseEntity<>(responseMessage,HttpStatus.OK);
    }
}
