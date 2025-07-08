package vn.lottefinance.landingpage.services;

public interface PhoneVerificationService {
    String generateToken(String phoneNumber);

    boolean verifyToken(String phoneNumber, String token);
}