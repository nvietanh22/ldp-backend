package vn.lottefinance.landingpage.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.lottefinance.landingpage.domain.PhoneVerificationToken;
import vn.lottefinance.landingpage.repository.PhoneVerificationTokenRepository;
import vn.lottefinance.landingpage.services.PhoneVerificationService;

import java.security.SecureRandom;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhoneVerificationServiceImpl implements PhoneVerificationService {
    private final PhoneVerificationTokenRepository tokenRepository;

 
}
