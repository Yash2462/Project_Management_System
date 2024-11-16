package com.projectmanagementsystembackend.controller;

import com.projectmanagementsystembackend.model.PlanType;
import com.projectmanagementsystembackend.model.User;
import com.projectmanagementsystembackend.service.UserService;
import com.projectmanagementsystembackend.vo.PaymentLinkResponse;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    Logger logger = (Logger) LoggerFactory.getLogger(PaymentController.class);
    @Value("${razorpay.api.key}")
    private String apiKey;
    @Value("${razorpay.api.secret}")
    private String apiSecret;
    @Autowired
    private UserService userService;

    @PostMapping("/{planType}")
    public ResponseEntity<PaymentLinkResponse> createPaymentLink(@PathVariable(value = "planType") PlanType planType,
                                                                 @RequestHeader("Authorization") String token) throws Exception {

        User user = userService.findUserProfileByJwt(token);
        int amount = 799*100;

        if (planType.equals(PlanType.ANNUALLY)){
            amount = amount * 12;
        }

        try {
            RazorpayClient razorpayClient = new RazorpayClient(apiKey,apiSecret);
            JSONObject paymentLinkRequest = new JSONObject();
            paymentLinkRequest.put("amount",amount);
            paymentLinkRequest.put("currency","INR");

            JSONObject customer = new JSONObject();
            customer.put("name",user.getFullName());
            customer.put("email",user.getEmail());
            paymentLinkRequest.put("customer",customer);

            JSONObject notify = new JSONObject();
            notify.put("email",true);
            paymentLinkRequest.put("notify",notify);

            paymentLinkRequest.put("callback_url","http://localhost:5173/upgrade_plan/success");

            PaymentLink paymentLink = razorpayClient.paymentLink.create(paymentLinkRequest);

            String paymentLinkId = paymentLink.get("id");
            String paymentLinkUrl = paymentLink.get("short_url");


            PaymentLinkResponse response = new PaymentLinkResponse();
            response.setGetPayment_link_id(paymentLinkId);
            response.setPayment_link_url(paymentLinkUrl);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RazorpayException e) {
            logger.info("Error in payment processing :"+e.getMessage());
            throw new RuntimeException(e);
        } catch (JSONException e) {
            logger.info("Error in payment processing :"+e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
