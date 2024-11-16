package com.projectmanagementsystembackend.service;

import com.projectmanagementsystembackend.model.PlanType;
import com.projectmanagementsystembackend.model.Subscription;
import com.projectmanagementsystembackend.model.User;

public interface SubscriptionService {

   Subscription createSubscription(User user);

   Subscription getUserSubscription(Long userId) throws Exception;

   Subscription updateSubscription(Long userId, PlanType planType);

   boolean isValid(Subscription subscription);
}
