package vn.lottefinance.landingpage.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.lottefinance.landingpage.client.custom.*;
import vn.lottefinance.landingpage.domain.MobileCard;
import vn.lottefinance.landingpage.dto.card.GetMobileCardRequestDTO;
import vn.lottefinance.landingpage.dto.card.GetMobileCardResponseDTO;
import vn.lottefinance.landingpage.dto.card.ValidateRequestDTO;
import vn.lottefinance.landingpage.dto.card.ValidateResponseDTO;
import vn.lottefinance.landingpage.enums.CardEnum;
import vn.lottefinance.landingpage.enums.ChannelEnum;
import vn.lottefinance.landingpage.exception.CustomedBadRequestException;
import vn.lottefinance.landingpage.repository.MobileCardRepository;
import vn.lottefinance.landingpage.repository.PhoneVerificationTokenRepository;
import vn.lottefinance.landingpage.services.ExcelService;
import vn.lottefinance.landingpage.services.MobileCardService;
import vn.lottefinance.landingpage.services.PhoneVerificationService;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MobileCardServiceImpl implements MobileCardService {

    private final MobileCardRepository repository;

    private final ExcelService excelService;

    private final PhoneVerificationService verificationService;

    private final PhoneVerificationTokenRepository tokenRepository;

    private final PsClient psClient;

    private final CardClient cardClient;

    private final DagorasClient dagorasClient;

    private final Loan02Client loan02Client;

    private final Loan03Client loan03Client;

    private final Loan04Client loan04Client;

    private final Loan05Client loan05Client;

    private final Loan07Client loan07Client;

    private final LoanClient loanClient;

    private final PlccClient plccClient;

    private final Loan01Client loan1Client;

    @Override
    public List<MobileCard> getAll() {
        return repository.findAll();
    }

    @Override
    public Optional<MobileCard> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public MobileCard create(MobileCard mobileCard) {
        return repository.save(mobileCard);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public GetMobileCardResponseDTO getCardNumber(GetMobileCardRequestDTO request) {
        boolean success = verificationService.verifyToken(request.getPhoneNumber(), request.getToken());
        if (!success) {
            throw new CustomedBadRequestException("Token is invalid or has expired");
        }
        MobileCard mobileCard = repository.findFirstByStatusAndBrandAndPrice(CardEnum.ACTIVE.getStatus(), request.getBrand(), request.getPrice());
        if (mobileCard == null) {
            throw new CustomedBadRequestException("Đã hết thẻ điện thoại, bạn vui lòng chọn nhà mạng khác!");
        }
        mobileCard.setStatus(CardEnum.INACTIVE.getStatus());
        mobileCard.setPhoneNumber(request.getPhoneNumber());
        mobileCard.setReceivedDate(new Date());
        repository.save(mobileCard);

        return GetMobileCardResponseDTO.builder()
                .cardNumber(mobileCard.getPartnerCode())
                .build();
    }

    @Override
    public void importMobileCardsFromExcel(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống");
        }

        List<MobileCard> cards = excelService.importFromExcel(file, MobileCard.class);

        if (cards == null || cards.isEmpty()) {
            throw new IllegalArgumentException("File không có dữ liệu hợp lệ");
        }

        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        for (MobileCard card : cards) {
            card.setStatus(CardEnum.ACTIVE.getStatus());
            card.setCreatedDate(now);
            if (card.getPrice() != null && !card.getPrice().isEmpty()) {
                try {
                    double parsedPrice = Double.parseDouble(card.getPrice());
                    long priceAsLong = (long) parsedPrice;
                    card.setPrice(String.valueOf(priceAsLong));
                } catch (NumberFormatException e) {
                    card.setPrice(card.getPrice().trim());
                }
            }
        }

        repository.saveAll(cards);
    }

    @Override
    public ValidateResponseDTO sendValidate(ValidateRequestDTO req, String channel) throws JsonProcessingException {
        log.info("Channel: {}", channel);
        log.info("Request: {}", req);

        ValidateResponseDTO response = new ValidateResponseDTO();

//        if (StringUtils.isEmpty(channel) || channel.equals(ChannelEnum.CARD.getVal())) {
//            response = (ValidateResponseDTO) cardClient.sendValidate(req, ValidateResponseDTO.class);
//        } else if (channel.equals(ChannelEnum.PLCC.getVal())) {
//            response = (ValidateResponseDTO) plccClient.sendValidate(req, ValidateResponseDTO.class);
//        } else if (channel.equals(ChannelEnum.PS.getVal())) {
//            response = (ValidateResponseDTO) psClient.sendValidate(req, ValidateResponseDTO.class);
//        } else if (channel.equals(ChannelEnum.LOAN.getVal())) {
//            response = (ValidateResponseDTO) loanClient.sendValidate(req, ValidateResponseDTO.class);
//        } else if (channel.equals(ChannelEnum.LOAN1.getVal())) {
//            response = (ValidateResponseDTO) loan1Client.sendValidate(req, ValidateResponseDTO.class);
//        } else if (channel.equals(ChannelEnum.LOAN2.getVal())) {
//            response = (ValidateResponseDTO) loan02Client.sendValidate(req, ValidateResponseDTO.class);
//        } else if (channel.equals(ChannelEnum.LOAN3.getVal())) {
//            response = (ValidateResponseDTO) loan03Client.sendValidate(req, ValidateResponseDTO.class);
//        } else if (channel.equals(ChannelEnum.LOAN4.getVal())) {
//            response = (ValidateResponseDTO) loan04Client.sendValidate(req, ValidateResponseDTO.class);
//        } else if (channel.equals(ChannelEnum.LOAN5.getVal())) {
//            response = (ValidateResponseDTO) loan05Client.sendValidate(req, ValidateResponseDTO.class);
//        } else if (channel.equals(ChannelEnum.LOAN7.getVal())) {
//            response = (ValidateResponseDTO) loan07Client.sendValidate(req, ValidateResponseDTO.class);
//        } else if (channel.equals(ChannelEnum.DAGORAS.getVal())) {
//            response = (ValidateResponseDTO) dagorasClient.sendValidate(req, ValidateResponseDTO.class);
//        } else {
//            log.error("Not found channel: {}", channel);
//            throw new CustomedBadRequestException("Not found channel");
//        }
        try {
            boolean exists = tokenRepository.existsPhoneNumber("0982941919");
            if (!exists) {
//            response.setRequest_id("141fc268-26a4-4cf7-90ea-5e8815592f91");
//            response.setReason_code("s");
////            response.setReason_code("f");
//            response.setRslt_cd("D");
//            response.setRslt_msg("Duplicate data");
                String token = verificationService.generateToken(req.getContact_number());
                response.setToken(token);
            } else {
                throw new CustomedBadRequestException("Bạn đã tham gia chương trình rồi.");
            }
        } catch (DataAccessException ex) {
            Throwable rootCause = ExceptionUtils.getRootCause(ex);
            if (rootCause instanceof SQLException) {
                System.err.println("Stored proc error: " + rootCause.getMessage());
            }
        }


        return response;
    }


}
