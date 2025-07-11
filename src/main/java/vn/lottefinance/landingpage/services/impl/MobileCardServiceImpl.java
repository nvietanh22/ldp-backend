package vn.lottefinance.landingpage.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import vn.lottefinance.landingpage.client.custom.*;
import vn.lottefinance.landingpage.dto.card.*;
import vn.lottefinance.landingpage.enums.CardEnum;
import vn.lottefinance.landingpage.exception.CustomedBadRequestException;
import vn.lottefinance.landingpage.services.ExcelService;
import vn.lottefinance.landingpage.services.MobileCardService;
import vn.lottefinance.landingpage.services.PhoneVerificationService;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class MobileCardServiceImpl implements MobileCardService {

    private final ExcelService excelService;

    private final PhoneVerificationService verificationService;

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

    @Autowired
    private EsbWareHouseClient esbClient;

    @Override
    public GetMobileCardResponseDTO getCardNumber(GetMobileCardRequestDTO request) {
        boolean success = verificationService.verifyToken(request.getPhoneNumber(), request.getToken());
        if (!success) {
            throw new CustomedBadRequestException("Token is invalid or has expired");
        }

        FindFirstByBrandAndPriceAndStatusRequestDTO findDTO = FindFirstByBrandAndPriceAndStatusRequestDTO.builder()
                .brand(request.getBrand())
                .price(request.getPrice())
                .status(CardEnum.ACTIVE.getStatus())
                .build();

        String mobileCardJson = esbClient.findFirstByBrandAndPriceAndStatus(findDTO);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

            MobileCardResponseDTO responseDTO = objectMapper.readValue(mobileCardJson, MobileCardResponseDTO.class);

            if (responseDTO.getStatusOut() == 1) {
                throw new CustomedBadRequestException("Đã hết thẻ điện thoại, bạn vui lòng chọn nhà mạng khác!");
            }

            // Convert responseDTO -> requestDTO
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            MobileCardRequestDTO requestDTO = MobileCardRequestDTO.builder()
                    .link(responseDTO.getLink())
                    .name(responseDTO.getName())
                    .sms(responseDTO.getSms())
                    .email(responseDTO.getEmail())
                    .partnerCode(responseDTO.getPartnerCode())
                    .gotitCode(responseDTO.getGotitCode())
                    .voucherSerial(responseDTO.getVoucherSerial())
                    .brand(request.getBrand())
                    .productName(responseDTO.getProductName())
                    .price(request.getPrice())
                    .issueDate(formatDate(responseDTO.getIssueDate(), formatter))
                    .expiredDate(formatDate(responseDTO.getExpiredDate(), formatter))
                    .transactionRefId(responseDTO.getTransRefId())
                    .poNumber(responseDTO.getPoNumber())
                    .status(CardEnum.INACTIVE.getStatus())
                    .phoneNumber(request.getPhoneNumber())
                    .receivedDate(formatDate(new Date(), formatter))
                    .build();

            esbClient.upsertMobileCard(requestDTO);

            return GetMobileCardResponseDTO.builder()
                    .cardNumber(requestDTO.getVoucherSerial())
                    .build();

        } catch (Exception e) {
            throw new CustomedBadRequestException("Lỗi xử lý dữ liệu mobile card: " + e.getMessage());
        }
    }


//
//    @Override
//    public void importMobileCardsFromExcel(MultipartFile file) throws Exception {
//        if (file == null || file.isEmpty()) {
//            throw new IllegalArgumentException("File không được để trống");
//        }
//
//        List<MobileCard> cards = excelService.importFromExcel(file, MobileCard.class);
//
//        if (cards == null || cards.isEmpty()) {
//            throw new IllegalArgumentException("File không có dữ liệu hợp lệ");
//        }
//
//        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//        for (MobileCard card : cards) {
//            card.setStatus(CardEnum.ACTIVE.getStatus());
//            card.setCreatedDate(now);
//            if (card.getPrice() != null && !card.getPrice().isEmpty()) {
//                try {
//                    double parsedPrice = Double.parseDouble(card.getPrice());
//                    long priceAsLong = (long) parsedPrice;
//                    card.setPrice(String.valueOf(priceAsLong));
//                } catch (NumberFormatException e) {
//                    card.setPrice(card.getPrice().trim());
//                }
//            }
//        }
//
//        repository.saveAll(cards);
//    }

    @Override
    public ValidateResponseDTO sendValidate(ValidateRequestDTO req, String channel) throws JsonProcessingException {
        log.info("Start Validate: {}", req);
        log.info("Channel: {}", channel);
        log.info("Request: {}", req);

        ValidateResponseDTO response = new ValidateResponseDTO();
//
//        // Gửi validate tới đúng client theo channel
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

        // Kiểm tra số điện thoại đã tham gia chưa
        try {
            CheckPhoneExitsRequestDTO checkPhoneDto = new CheckPhoneExitsRequestDTO(req.getContact_number());
            String res = esbClient.checkPhoneExitsOnlyData(checkPhoneDto);

            JSONObject jsonObject = new JSONObject(res);
            int status = jsonObject.optInt("status");
            String message = jsonObject.optString("message");

            if ("Bạn đã tham gia chương trình rồi".equalsIgnoreCase(message)) {
                throw new CustomedBadRequestException("Bạn đã tham gia chương trình rồi.");
            }

            // Nếu chưa tham gia thì sinh token
            String token = verificationService.generateToken(req.getContact_number());
            response.setToken(token);

        } catch (DataAccessException ex) {
            Throwable rootCause = ExceptionUtils.getRootCause(ex);
            if (rootCause instanceof SQLException) {
                System.err.println("Stored proc error: " + rootCause.getMessage());
            }
            throw ex;
        } catch (JSONException jsonEx) {
            log.error("Lỗi parse JSON response từ esbClient: {}", jsonEx.getMessage());
            throw new RuntimeException("Lỗi xử lý dữ liệu từ hệ thống kiểm tra số điện thoại");
        }

        return response;
    }

    private String formatDate(Date date, SimpleDateFormat formatter) {
        return date != null ? formatter.format(date) : null;
    }

}
