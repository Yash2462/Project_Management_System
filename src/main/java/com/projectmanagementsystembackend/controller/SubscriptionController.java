package com.projectmanagementsystembackend.controller;

import com.projectmanagementsystembackend.model.PlanType;
import com.projectmanagementsystembackend.model.Subscription;
import com.projectmanagementsystembackend.model.User;
import com.projectmanagementsystembackend.service.SubscriptionService;
import com.projectmanagementsystembackend.service.UserService;
import com.projectmanagementsystembackend.vo.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private UserService userService;

    @GetMapping("/user")
    public ResponseEntity<Object> getUserSubscription(@RequestHeader("Authorization") String token) throws Exception {

        User user = userService.findUserProfileByJwt(token);
        Subscription subscription = subscriptionService.getUserSubscription(user.getId());
        ResponseMessage responseMessage = new ResponseMessage();
        if (subscription == null){
            responseMessage.setMessage("User Does Not Have Any Subscription");
            responseMessage.setStatus(404);

            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }

        responseMessage.setMessage("Subscription Found Successfully");
        responseMessage.setStatus(200);
        responseMessage.setData(subscription);

        return new ResponseEntity<>(responseMessage,HttpStatus.OK);
    }


    @PutMapping("/upgrade")
    public ResponseEntity<Object> upgradeSubscription(@RequestHeader("Authorization") String token,
                                                      @RequestParam PlanType planType) throws Exception {

        User user = userService.findUserProfileByJwt(token);
        Subscription subscription = subscriptionService.updateSubscription(user.getId(), planType);
        ResponseMessage responseMessage = new ResponseMessage();
        if (subscription == null){
            responseMessage.setMessage("Error in upgrading Subscription");
            responseMessage.setStatus(500);

            return new ResponseEntity<>(responseMessage,HttpStatus.INTERNAL_SERVER_ERROR);
        }

        responseMessage.setMessage("Plan upgraded successfully");
        responseMessage.setStatus(200);
        responseMessage.setData(subscription);

        return new ResponseEntity<>(responseMessage,HttpStatus.OK);
    }
}
