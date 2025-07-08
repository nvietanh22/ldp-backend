package vn.lottefinance.landingpage.config.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class ApiKeyAuthManager implements AuthenticationManager {

    private String apiKey = "Dejwak-mevped-8zoffi";

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String principal = (String) authentication.getPrincipal();
        if (apiKey.equals(principal)) {
            authentication.setAuthenticated(true);
            return authentication;
        } else {
            throw new BadCredentialsException("The API key was not found or not the expected value.");
        }
    }

}