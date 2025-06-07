package com.projectmanagementsystembackend.service;


public interface OtpService {
    /**
     * Generates a one-time password (OTP) for the given email.
     *
     * @param email the email address to generate the OTP for
     * @return the generated OTP
     */
    String generateOtp(String email);

    /**
     * Validates the provided OTP against the stored OTP for the given email.
     *
     * @param email the email address associated with the OTP
     * @param otp   the OTP to validate
     * @return true if the OTP is valid, false otherwise
     */
    boolean validateOtp(String email, String otp);
}
