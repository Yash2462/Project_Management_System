package com.projectmanagementsystembackend.repository;

import com.projectmanagementsystembackend.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription,Long> {

    Subscription findByUserId(Long userId);
}
