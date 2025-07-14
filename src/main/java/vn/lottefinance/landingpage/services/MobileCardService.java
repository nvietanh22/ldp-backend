package vn.lottefinance.landingpage.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.multipart.MultipartFile;
import vn.lottefinance.landingpage.dto.card.GetMobileCardRequestDTO;
import vn.lottefinance.landingpage.dto.card.GetMobileCardResponseDTO;
import vn.lottefinance.landingpage.dto.card.ValidateRequestDTO;
import vn.lottefinance.landingpage.dto.card.ValidateResponseDTO;

public interface MobileCardService {
    GetMobileCardResponseDTO getCardNumber(GetMobileCardRequestDTO request);

    void importMobileCardsFromExcel(MultipartFile file) throws Exception;

    ValidateResponseDTO sendValidate(ValidateRequestDTO request, String channel) throws JsonProcessingException;

}

