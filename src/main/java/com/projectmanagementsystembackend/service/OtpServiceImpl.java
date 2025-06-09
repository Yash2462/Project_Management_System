package com.projectmanagementsystembackend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class OtpServiceImpl implements OtpService {

    private static final int OTP_EXPIRATION_MINUTES = 15;
    private static final int OTP_CLEANUP_THRESHOLD_MINUTES = 60; // 1 hour
    private static final int MAX_ATTEMPTS = 3;

    private static class OtpDetails {
        String otp;
        LocalDateTime timestamp;
        int failedAttempts;

        OtpDetails(String otp, LocalDateTime timestamp) {
            this.otp = otp;
            this.timestamp = timestamp;
            this.failedAttempts = 0;
        }
    }

    // Use thread-safe ConcurrentHashMap
    private static final Map<String, OtpDetails> otpStorage = new ConcurrentHashMap<>();
    private final Random random = new Random();

    @Override
    public String generateOtp(String email) {
        OtpDetails existing = otpStorage.get(email);
        if (existing != null && !isExpired(existing, OTP_EXPIRATION_MINUTES)) {
            return existing.otp;
        }

        String otp = String.format("%06d", random.nextInt(999999));
        otpStorage.put(email, new OtpDetails(otp, LocalDateTime.now()));
        log.info("Generated OTP for {}: {}", email, otp);
        return otp;
    }

    @Override
    public boolean validateOtp(String email, String otp) {
        OtpDetails details = otpStorage.get(email);

        if (details == null || isExpired(details, OTP_EXPIRATION_MINUTES)) {
            otpStorage.remove(email);
            log.info("OTP for {} is expired or does not exist", email);
            return false;
        }

        if (details.otp.equals(otp)) {
            log.info("OTP for {} validated successfully", email);
            otpStorage.remove(email);
            return true;
        } else {
            details.failedAttempts++;
            if (details.failedAttempts >= MAX_ATTEMPTS) {
                log.warn("Max attempts reached for {}. OTP will be removed.", email);
                //remove OTP after max attempts to prevent brute force attacks
                otpStorage.remove(email);
            }
            log.info("Invalid OTP for {}. Failed attempts: {}", email, details.failedAttempts);
            return false;
        }
    }

    private boolean isExpired(OtpDetails details, int minutes) {
        return details.timestamp.plusMinutes(minutes).isBefore(LocalDateTime.now());
    }

    // 🔁 Cleanup task runs every 10 minutes
    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void cleanUpOldOtps() {
        log.info("Running OTP cleanup task");
        otpStorage.entrySet().removeIf(entry -> isExpired(entry.getValue(), OTP_CLEANUP_THRESHOLD_MINUTES));
    }
}
