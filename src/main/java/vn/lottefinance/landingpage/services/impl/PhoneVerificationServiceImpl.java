package vn.lottefinance.landingpage.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.lottefinance.landingpage.client.custom.EsbWareHouseClient;
import vn.lottefinance.landingpage.dto.card.FindPhoneAndTokenRequestDTO;
import vn.lottefinance.landingpage.dto.card.PhoneTokenResponseDTO;
import vn.lottefinance.landingpage.dto.card.PhoneVerifyTokenRequestDTO;
import vn.lottefinance.landingpage.exception.CustomedBadRequestException;
import vn.lottefinance.landingpage.services.PhoneVerificationService;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhoneVerificationServiceImpl implements PhoneVerificationService {


    private final EsbWareHouseClient esbWareHouseClient;

    @Override
    public String generateToken(String phoneNumber) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);

        PhoneVerifyTokenRequestDTO entity = new PhoneVerifyTokenRequestDTO();
        entity.setPhoneNumber(phoneNumber);
        entity.setToken(token);
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        ZonedDateTime expired = now.plusMinutes(50000);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        entity.setCreatedAt(now.format(formatter));
        entity.setExpiredAt(expired.format(formatter));
        entity.setVerified(0);

        esbWareHouseClient.upsertPhoneToken(entity);
        return token;
    }

    @Override
    public boolean verifyToken(String phoneNumber, String token) {
        FindPhoneAndTokenRequestDTO requestDTO = FindPhoneAndTokenRequestDTO.builder()
                .phoneNumber(phoneNumber)
                .token(token)
                .build();

        String json = esbWareHouseClient.findPhoneToken(requestDTO);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            PhoneTokenResponseDTO response = objectMapper.readValue(json, PhoneTokenResponseDTO.class);

            // 👉 Check nếu không tìm thấy dữ liệu phù hợp
            if (response.getStatus() == 1 &&
                    "Không tìm thấy dữ liệu phù hợp".equalsIgnoreCase(response.getMessage())) {
                log.warn("Sai thông tin của khách hàng: {}", requestDTO);
                return false;
            }

            Date createdAtDate = response.getCreatedAt();
            Date expiredAtDate = response.getExpiredAt();

            if (createdAtDate == null || expiredAtDate == null) {
                throw new CustomedBadRequestException("Thiếu thông tin thời gian tạo hoặc hết hạn token.");
            }

            if (response.getStatus() == 0 &&
                    !response.isVerifyed() &&
                    expiredAtDate.after(new Date())) {

                return true;
            }

        } catch (Exception e) {
            throw new CustomedBadRequestException(e.getMessage());
        }


        return false;
    }


}
