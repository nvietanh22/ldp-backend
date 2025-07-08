package vn.lottefinance.landingpage.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.validation.constraints.Email;

public class ValidationExtendtion {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";
    private static final String PHONE_REGEX = "^[0-9]{10,11}$";

    public static boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidPhone(String phone) {
        Pattern pattern = Pattern.compile(PHONE_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    
}
