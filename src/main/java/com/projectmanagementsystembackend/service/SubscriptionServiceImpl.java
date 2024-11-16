package com.projectmanagementsystembackend.service;

import com.projectmanagementsystembackend.model.PlanType;
import com.projectmanagementsystembackend.model.Subscription;
import com.projectmanagementsystembackend.model.User;
import com.projectmanagementsystembackend.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SubscriptionServiceImpl implements SubscriptionService{
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private UserService userService;
    @Override
    public Subscription createSubscription(User user) {
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setSubscriptionStartDate(LocalDate.now());
        subscription.setSubscriptionEndDate(LocalDate.now().plusMonths(12));
        subscription.setValid(true);
        subscription.setPlanType(PlanType.FREE);

        return subscriptionRepository.save(subscription);
    }

    @Override
    public Subscription getUserSubscription(Long userId) throws Exception {
        Subscription subscription = subscriptionRepository.findByUserId(userId);
        if (subscription.isValid()){
            subscription.setPlanType(PlanType.FREE);
            subscription.setSubscriptionEndDate(LocalDate.now().plusMonths(12));
            subscription.setSubscriptionStartDate(LocalDate.now());
        }
        return subscription;
    }

    @Override
    public Subscription updateSubscription(Long userId, PlanType planType) {
     Subscription subscription = subscriptionRepository.findByUserId(userId);
     subscription.setPlanType(planType);
     subscription.setSubscriptionStartDate(LocalDate.now());
     if(planType.equals(PlanType.ANNUALLY)){
         subscription.setSubscriptionEndDate(LocalDate.now().plusMonths(12));
     }else {
         subscription.setSubscriptionEndDate(LocalDate.now().plusMonths(1));
     }
        return subscriptionRepository.save(subscription);
    }

    @Override
    public boolean isValid(Subscription subscription) {
        if (subscription.getPlanType().equals(PlanType.FREE)){
            return true;
        }
        LocalDate endDate = subscription.getSubscriptionEndDate();
        LocalDate now = LocalDate.now();
        return endDate.isAfter(now) || endDate.isEqual(now);
    }
}
