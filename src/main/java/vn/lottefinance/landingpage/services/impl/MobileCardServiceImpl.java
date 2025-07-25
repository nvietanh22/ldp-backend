package vn.lottefinance.landingpage.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import vn.lottefinance.landingpage.client.custom.*;
import vn.lottefinance.landingpage.dto.card.*;
import vn.lottefinance.landingpage.enums.CardEnum;
import vn.lottefinance.landingpage.enums.ChannelEnum;
import vn.lottefinance.landingpage.exception.CustomedBadRequestException;
import vn.lottefinance.landingpage.services.CacheService;
import vn.lottefinance.landingpage.services.ExcelService;
import vn.lottefinance.landingpage.services.MobileCardService;
import vn.lottefinance.landingpage.services.PhoneVerificationService;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Collections;
import java.util.UUID;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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

    private final EsbWareHouseClient esbClient;
    private final CacheService cacheService;

    private final LuckyWheelConfig luckyWheelConfig;

    private Map<String, Double> prizeRatio;
    private Map<String, String> prizeNameMap;

    @Component
    @ConfigurationProperties(prefix = "lucky-wheel")
    @Data
    public static class LuckyWheelConfig {
        private List<PrizeConfig> prizes;
    }

    @Data
    public static class PrizeConfig {
        private String value;
        private String name;
        private double ratio;
    }

    @PostConstruct
    public void init() {
        prizeRatio = new HashMap<>();
        prizeNameMap = new HashMap<>();

        if (luckyWheelConfig.getPrizes() == null) {
            log.error("Cấu hình vòng quay 'lucky-wheel.prizes' bị thiếu trong application.yaml!");
            return;
        }

        for (PrizeConfig config : luckyWheelConfig.getPrizes()) {
            prizeRatio.put(config.getValue(), config.getRatio());
            if (!"unlucky".equals(config.getValue())) {
                prizeNameMap.put(config.getValue(), config.getName());
            }
        }

        log.info("Cấu hình Vòng quay May mắn đã được tải thành công.");
    }

    @Override
    public GetMobileCardResponseDTO getCardNumber(GetMobileCardRequestDTO request) {
        boolean success = verificationService.verifyToken(request.getPhoneNumber(), request.getToken());
        if (!success) {
            throw new CustomedBadRequestException("Token is invalid or has expired");
        }

        String cacheKey = "minigame:card:" + request.getPhoneNumber();
        String cardNumber = cacheService.getFromCache(cacheKey)
                .orElseThrow(() -> new CustomedBadRequestException("Không tìm thấy kết quả quay thưởng. Vui lòng thử lại."));

        cacheService.deleteCache(cacheKey);

        if ("unlucky".equals(cardNumber)) {
            throw new CustomedBadRequestException("Người dùng không trúng thưởng.");
        }

        return GetMobileCardResponseDTO.builder()
                .cardNumber(cardNumber)
                .price(request.getPrice())
                .rslt_msg("Success")
                .reason_code("0")
                .build();
    }


    @Override
    public void importMobileCardsFromExcel(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống");
        }

        List<MobileCardRequestDTO> cards = excelService.importFromExcel(file, MobileCardRequestDTO.class);
        if (cards == null || cards.isEmpty()) {
            throw new IllegalArgumentException("File không có dữ liệu hợp lệ");
        }

        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        List<String> errorLogs = new ArrayList<>();

        for (int i = 0; i < cards.size(); i++) {
            MobileCardRequestDTO card = cards.get(i);
            try {
                prepareCard(card, now);
                esbClient.upsertMobileCard(card);
            } catch (Exception e) {
                log.error("Lỗi khi xử lý bản ghi thứ {}: {}", i + 1, card, e);
                errorLogs.add("Dòng " + (i + 1) + ": " + e.getMessage());
            }
        }

        if (!errorLogs.isEmpty()) {
            throw new CustomedBadRequestException("Có lỗi khi import:\n" + String.join("\n", errorLogs));
        }
    }


    @Override
    public ValidateResponseDTO sendValidate(ValidateRequestDTO req, String channel) throws JsonProcessingException {
        log.info("Start Validate: {}", req);
        log.info("Channel: {}", channel);
        log.info("Request: {}", req);

        ValidateResponseDTO response = new ValidateResponseDTO();
        response.setRequest_id(req.getRequest_id());
        response.setReason_code("0");
        response.setRslt_msg("Success");
        response.setRslt_cd("s");
//
//        // Gửi validate tới đúng client theo channel
        if (StringUtils.isEmpty(channel) || channel.equals(ChannelEnum.CARD.getVal())) {
            response = (ValidateResponseDTO) cardClient.sendValidate(req, ValidateResponseDTO.class);
        }
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

            String message = jsonObject.optString("message");

            // Lấy thông tin từ response validate
            JSONObject jsonValidate = new JSONObject(new ObjectMapper().writeValueAsString(response));
            String rsltCd = jsonValidate.optString("rslt_cd");
            String reasonCode = jsonValidate.optString("reason_code");

            // Check điều kiện throw
            if ("Bạn đã tham gia chương trình rồi".equalsIgnoreCase(message)) {
                throw new CustomedBadRequestException("Bạn đã tham gia chương trình rồi.");
            }

            // Nếu không throw thì sinh token
            String token = verificationService.generateToken(req.getContact_number());
            response.setToken(token);
            response.setRslt_msg("Success");

            // Gắn thông điệp theo điều kiện
            if ("f".equalsIgnoreCase(rsltCd) && "D".equalsIgnoreCase(reasonCode)) {
                response.setRslt_cd("s");
                response.setReason_code("0");
                response.setStatus(0);
            } else {
                response.setRslt_cd("s");
                response.setReason_code("0");
                response.setStatus(1);
            }

            return response;

        } catch (DataAccessException ex) {
            Throwable rootCause = ExceptionUtils.getRootCause(ex);
            if (rootCause instanceof SQLException) {
                log.error("Stored proc error: " + rootCause.getMessage());
            }
            throw ex;
        } catch (JSONException jsonEx) {
            log.error("Lỗi parse JSON response từ esbClient: {}", jsonEx.getMessage());
            throw new CustomedBadRequestException("Lỗi xử lý dữ liệu từ hệ thống kiểm tra số điện thoại");
        }


    }

    private String formatDate(Date date, SimpleDateFormat formatter) {
        return date != null ? formatter.format(date) : null;
    }

    private void prepareCard(MobileCardRequestDTO card, String createdDate) {
        card.setStatus(CardEnum.ACTIVE.getStatus());
        card.setCreatedDate(createdDate);

        // Chuẩn hóa price nếu là số
        String rawPrice = card.getPrice();
        if (rawPrice != null && !rawPrice.trim().isEmpty()) {
            try {
                double parsed = Double.parseDouble(rawPrice.trim());
                card.setPrice(String.valueOf((long) parsed));
            } catch (NumberFormatException e) {
                card.setPrice(rawPrice.trim());
            }
        }

        // Convert issueDate nếu là số serial từ Excel
        card.setIssueDate(convertExcelSerialToDate(card.getIssueDate()));
        card.setExpiredDate(convertExcelSerialToDate(card.getExpiredDate()));
    }

    private String convertExcelSerialToDate(String value) {
        if (value == null || value.trim().isEmpty()) return null;

        try {
            double excelDate = Double.parseDouble(value.trim());
            Date date = DateUtil.getJavaDate(excelDate);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(date);
        } catch (NumberFormatException e) {
            // Trường hợp không phải dạng số, giữ nguyên
            return value.trim();
        }
    }

    private static final int TOTAL_SLOTS = 10;

    @Override
    public SpinResultResponseDTO getSpinResult(SpinResultRequestDTO request) {
        boolean isTokenValid = verificationService.verifyToken(request.getPhoneNumber(), request.getToken());
        if (!isTokenValid) {
            throw new CustomedBadRequestException("Token is invalid or has expired");
        }

        String cacheKey = "minigame:layout:" + request.getLayoutId();
        String layoutJson = cacheService.getFromCache(cacheKey)
                .orElseThrow(() -> new CustomedBadRequestException("Vòng quay đã hết hạn hoặc không hợp lệ."));

        cacheService.deleteCache(cacheKey);

        List<PrizeSegmentDTO> frontendLayout;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            frontendLayout = objectMapper.readValue(layoutJson, new TypeReference<List<PrizeSegmentDTO>>() {});
        } catch (JsonProcessingException e) {
            throw new CustomedBadRequestException("Lỗi đọc dữ liệu vòng quay.");
        }

        GetListCardActiveByBrandRequestDTO priceRequest = new GetListCardActiveByBrandRequestDTO(request.getBrand());
        GetCardResponseDTO priceResponse = esbClient.getActivePriceByBrandService(priceRequest);
        List<String> availablePrizes = (priceResponse.getPrices() != null && !priceResponse.getPrices().isEmpty())
                ? Arrays.asList(priceResponse.getPrices().split(","))
                : new ArrayList<>();

        PrizeSegmentDTO winningSegment = determineWinningSegmentFromLayout(frontendLayout, availablePrizes);

        String cardCacheKey = "minigame:card:" + request.getPhoneNumber();
        String prizeValue = winningSegment.getValue();

        PhoneVerifyTokenRequestDTO phoneVerifyTokenRequestDTO = PhoneVerifyTokenRequestDTO.builder()
                .verified(1)
                .phoneNumber(request.getPhoneNumber())
                .token(request.getToken())
                .build();
        esbClient.upsertPhoneToken(phoneVerifyTokenRequestDTO);

        if (prizeValue != null) {
            try {
                FindFirstByBrandAndPriceAndStatusRequestDTO findDTO = FindFirstByBrandAndPriceAndStatusRequestDTO.builder()
                        .brand(request.getBrand())
                        .price(prizeValue)
                        .status(CardEnum.ACTIVE.getStatus())
                        .build();
                String mobileCardJson = esbClient.findFirstByBrandAndPriceAndStatus(findDTO);

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
                MobileCardResponseDTO responseDTO = objectMapper.readValue(mobileCardJson, MobileCardResponseDTO.class);

                if (responseDTO.getStatusOut() == 1) {
                    throw new CustomedBadRequestException("Đã hết thẻ điện thoại cho giải thưởng này!");
                }

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                MobileCardRequestDTO cardUpdateRequest = MobileCardRequestDTO.builder()
                        .link(responseDTO.getLink()).name(responseDTO.getName()).sms(responseDTO.getSms())
                        .email(responseDTO.getEmail()).partnerCode(responseDTO.getPartnerCode())
                        .gotitCode(responseDTO.getGotitCode()).voucherSerial(responseDTO.getVoucherSerial())
                        .brand(request.getBrand()).productName(responseDTO.getProductName())
                        .price(prizeValue).issueDate(formatDate(responseDTO.getIssueDate(), formatter))
                        .expiredDate(formatDate(responseDTO.getExpiredDate(), formatter))
                        .transactionRefId(responseDTO.getTransRefId()).poNumber(responseDTO.getPoNumber())
                        .status(CardEnum.INACTIVE.getStatus()) // Đánh dấu đã sử dụng
                        .phoneNumber(request.getPhoneNumber()) // Gắn số điện thoại người nhận
                        .receivedDate(formatDate(new Date(), formatter))
                        .build();
                esbClient.upsertMobileCard(cardUpdateRequest);

                cacheService.putInCache(cardCacheKey, responseDTO.getVoucherSerial());
                log.info("Đã lưu thẻ {} vào cache cho SĐT {}", responseDTO.getVoucherSerial(), request.getPhoneNumber());

            } catch (Exception e) {
                log.error("Lỗi khi lấy và cập nhật thẻ trong getSpinResult: {}", e.getMessage());
                winningSegment = frontendLayout.stream().filter(s -> s.getValue() == null).findFirst().orElse(winningSegment);
                cacheService.putInCache(cacheKey, "unlucky");
            }
        } else {
            cacheService.putInCache(cardCacheKey, "unlucky");
            log.info("Người dùng {} không trúng thưởng, đã lưu unlucky vào cache.", request.getPhoneNumber());
        }

        String prizeName = winningSegment.getValue() != null
                ? prizeNameMap.getOrDefault(winningSegment.getValue(), "N/A")
                : "unlucky";

        return SpinResultResponseDTO.builder()
                .prize(winningSegment.getValue())
//                .prizeName(prizeName)
                .targetIndex(winningSegment.getIndex())
                .rslt_cd("s")
                .rslt_msg("Success")
                .reason_code("0")
                .build();
    }

    private PrizeSegmentDTO determineWinningSegmentFromLayout(List<PrizeSegmentDTO> layout, List<String> availablePrizes) {
        List<PrizeSegmentDTO> weightedList = new ArrayList<>();
        prizeRatio.forEach((prizeKey, ratio) -> {
            boolean isUnlucky = "unlucky".equals(prizeKey);
            if ((!isUnlucky && availablePrizes.contains(prizeKey)) || isUnlucky) {
                List<PrizeSegmentDTO> matchingSegments = layout.stream()
                        .filter(s -> Objects.equals(s.getValue(), isUnlucky ? null : prizeKey))
                        .collect(Collectors.toList());

                if (!matchingSegments.isEmpty()) {
                    int weight = (int) (ratio * 1000);
                    for (int i = 0; i < weight; i++) {
                        weightedList.add(matchingSegments.get(new Random().nextInt(matchingSegments.size())));
                    }
                }
            }
        });

        if (weightedList.isEmpty()) {
            List<PrizeSegmentDTO> losingSegments = layout.stream()
                    .filter(s -> s.getValue() == null)
                    .collect(Collectors.toList());

            if (!losingSegments.isEmpty()) {
                return losingSegments.get(new Random().nextInt(losingSegments.size()));
            } else {
                return layout.get(new Random().nextInt(layout.size()));
            }
        }

        return weightedList.get(new Random().nextInt(weightedList.size()));
    }

    private List<PrizeSegmentDTO> createRandomizedLayout(List<String> availablePrizes) {
        final int TOTAL_SLOTS = 10;
        List<PrizeSegmentDTO> distributedPrizes = new ArrayList<>();
        List<String> validPrizeTypes = prizeRatio.keySet().stream()
                .filter(p -> !"unlucky".equals(p) && availablePrizes.contains(p) && prizeRatio.get(p) > 0)
                .collect(Collectors.toList());

        double totalAvailableRatio = validPrizeTypes.stream().mapToDouble(prizeRatio::get).sum();
        double unluckyRatio = prizeRatio.getOrDefault("unlucky", 0.0);
        double totalRatio = totalAvailableRatio + unluckyRatio;

        int slotsFilled = 0;

        for (String prize : validPrizeTypes) {
            double normalizedRatio = prizeRatio.get(prize) / totalRatio;
            int slotsForThisPrize = (int) Math.round(normalizedRatio * TOTAL_SLOTS);
            for (int i = 0; i < slotsForThisPrize; i++) {
                if (slotsFilled < TOTAL_SLOTS) {
                    distributedPrizes.add(new PrizeSegmentDTO(0, prize));
                    slotsFilled++;
                }
            }
        }

        while (slotsFilled < TOTAL_SLOTS) {
            distributedPrizes.add(new PrizeSegmentDTO(0, null));
            slotsFilled++;
        }

        Collections.shuffle(distributedPrizes);

        for (int i = 0; i < distributedPrizes.size(); i++) {
            distributedPrizes.get(i).setIndex(i);
        }
        return distributedPrizes;
    }

    @Override
    public WheelLayoutResponseDTO generateAndCacheLayout(String brand) {
        GetListCardActiveByBrandRequestDTO priceRequest = new GetListCardActiveByBrandRequestDTO(brand);
        GetCardResponseDTO priceResponse = esbClient.getActivePriceByBrandService(priceRequest);
        List<String> availablePrizes = (priceResponse.getPrices() != null && !priceResponse.getPrices().isEmpty())
                ? Arrays.asList(priceResponse.getPrices().split(","))
                : new ArrayList<>();

        List<PrizeSegmentDTO> layout = createRandomizedLayout(availablePrizes);
        String layoutId = UUID.randomUUID().toString();
        String cacheKey = "minigame:layout:" + layoutId;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String layoutJson = objectMapper.writeValueAsString(layout);
            cacheService.putInCache(cacheKey, layoutJson);
        } catch (JsonProcessingException e) {
            log.error("Không thể serialize wheelLayout: {}", e.getMessage());
            throw new CustomedBadRequestException("Lỗi hệ thống khi tạo vòng quay.");
        }

        return WheelLayoutResponseDTO.builder()
                .wheelLayout(layout)
                .layoutId(layoutId)
                .rslt_cd("s")
                .rslt_msg("Success")
                .build();
    }
}
