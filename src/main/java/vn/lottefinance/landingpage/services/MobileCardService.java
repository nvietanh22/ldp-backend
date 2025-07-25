package vn.lottefinance.landingpage.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
import vn.lottefinance.landingpage.dto.card.*;

public interface MobileCardService {
    GetMobileCardResponseDTO getCardNumber(GetMobileCardRequestDTO request);

    void importMobileCardsFromExcel(MultipartFile file) throws Exception;

    ValidateResponseDTO sendValidate(ValidateRequestDTO request, String channel) throws JsonProcessingException;
    SpinResultResponseDTO getSpinResult(SpinResultRequestDTO request);
    WheelLayoutResponseDTO generateAndCacheLayout(String brand);
}

