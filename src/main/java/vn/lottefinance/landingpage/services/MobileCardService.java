package vn.lottefinance.landingpage.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.multipart.MultipartFile;
import vn.lottefinance.landingpage.domain.MobileCard;
import vn.lottefinance.landingpage.dto.card.GetMobileCardRequestDTO;
import vn.lottefinance.landingpage.dto.card.GetMobileCardResponseDTO;
import vn.lottefinance.landingpage.dto.card.ValidateRequestDTO;
import vn.lottefinance.landingpage.dto.card.ValidateResponseDTO;

import java.util.List;
import java.util.Optional;

public interface MobileCardService {
    List<MobileCard> getAll();

    Optional<MobileCard> getById(Long id);

    MobileCard create(MobileCard mobileCard);

    void delete(Long id);

    GetMobileCardResponseDTO getCardNumber(GetMobileCardRequestDTO request);

    void importMobileCardsFromExcel(MultipartFile file) throws Exception;

    ValidateResponseDTO sendValidate(ValidateRequestDTO request, String channel) throws JsonProcessingException;

}

