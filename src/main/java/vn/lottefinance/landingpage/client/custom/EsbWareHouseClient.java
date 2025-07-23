package vn.lottefinance.landingpage.client.custom;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.lottefinance.landingpage.dto.CustomerInfoRequestDto;
import vn.lottefinance.landingpage.dto.DataDto;
import vn.lottefinance.landingpage.dto.InviteInfo;
import vn.lottefinance.landingpage.dto.ResponseDto;
import vn.lottefinance.landingpage.dto.card.*;
import vn.lottefinance.landingpage.enums.ChannelEnum;
import vn.lottefinance.landingpage.enums.OtpVerifyStatusEnum;
import vn.lottefinance.landingpage.exception.CustomedBadRequestException;
import vn.lottefinance.landingpage.properties.EsbProperties;
import vn.lottefinance.landingpage.services.CacheService;
import vn.lottefinance.landingpage.utils.SSLSocketUtil;

import java.io.IOException;
import java.time.Year;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;


@Component
@Slf4j
public class EsbWareHouseClient {
    @Autowired
    private EsbProperties esbProperties;
    private OkHttpClient okHttpClient;

    @Value("${api.baseUrl}")
    private String esbBaseUrl;

    @Value("${api.endpoint.inquiry}")
    private String inquiryService;

    @Value("${api.username}")
    private String esbUser;

    @Value("${api.password}")
    private String esbPass;

    @Value("${card.baseUrl}")
    private String cardUrl;

    @Value("${card.endpoint.checkExit}")
    private String checkExitService;

    @Value("${card.endpoint.upsertPhoneToken}")
    private String upsertPhoneTokenService;

    @Value("${card.endpoint.findPhoneAndToken}")
    private String findPhoneAndTokenService;

    @Value("${card.endpoint.findFirstStatusBrandPrice}")
    private String findFirstStatusBrandPriceService;

    @Value("${card.endpoint.upsertMobileCard}")
    private String upsertMobileCardService;

    @Value("${card.endpoint.getActivePriceByBrand}")
    private String getActivePriceByBrandService;

    @Autowired
    private CacheService cacheService;

    @PostConstruct
    public void buildClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder().header("User-Agent", "Mozilla/5.0").header("Accept-Language", "en-US,en;q=0.5").header("Authorization", Credentials.basic(esbUser, esbPass));
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });
        httpClient.addNetworkInterceptor(logging);
        this.okHttpClient = SSLSocketUtil.trustAllSslClient(httpClient.build());
    }

    public ResponseDto sentWareHouse(DataDto dto, String channel) {
        log.info("sentWareHouse Request: " + dto.toString());
        CustomerInfoRequestDto customerInfoRequestDto = new CustomerInfoRequestDto();
        customerInfoRequestDto.setTransId(UUID.randomUUID().toString());
        if (dto.getInviteList() != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<InviteInfo> inviteInfos = mapper.readValue(dto.getInviteList(), new TypeReference<List<InviteInfo>>() {
                });
                for (InviteInfo invite : inviteInfos) {
                    log.info("Invite: {} - {}", invite.getName(), invite.getPhone());
                }

                String inviteJson = mapper.writeValueAsString(inviteInfos);
                log.info("Converted InviteList to JSON: {}", inviteJson);

                dto.setInviteList(inviteJson);

            } catch (IOException e) {
                throw new CustomedBadRequestException("Invalid inviteList JSON", e);
            }
        }
        customerInfoRequestDto.setData(dto);
        ResponseDto responseDto = new ResponseDto();
        if (channel != ChannelEnum.LOAN7.getVal()) {
            ResponseDto idValidationResult = validateVietnameseID(dto.getIdCard());
            if (!"Success".equals(idValidationResult.getRslt_msg())) {
                return idValidationResult;
            }
            ResponseDto phoneValidationResult = validateVietnamesePhoneNumber(dto.getPhoneNumber());
            if (!"Success".equals(phoneValidationResult.getRslt_msg())) {
                return phoneValidationResult;
            }
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequestBody;
        try {
            jsonRequestBody = objectMapper.writeValueAsString(customerInfoRequestDto);
        } catch (IOException e) {
            throw new CustomedBadRequestException("Error serializing DTO", e);
        }
        if (channel != ChannelEnum.LOAN7.getVal()) {
            String key = String.format("%s_%s", dto.getPhoneNumber(), dto.getIdCard());
            log.info("putInCache: {}", key);
            cacheService.putInCache(key, OtpVerifyStatusEnum.DONE.getVal());
        } else {
            String key = String.format("%s_%s", dto.getPhoneNumber(), channel);
            log.info("putInCache: {}", key);
            cacheService.putInCache(key, OtpVerifyStatusEnum.DONE.getVal());
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonRequestBody);
        String url = esbBaseUrl + inquiryService;
        Request request = new Request.Builder().url(url).post(requestBody).build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            String respone = response.body().string();
            JSONObject jsonObject = new JSONObject(respone);
            responseDto.setReason_code(jsonObject.optString("ErrorCode"));
            if (jsonObject.optString("ErrorMessage").equals("Successful")) {
                responseDto.setRslt_msg("Success");
                log.info("Start create cache: {}");
            } else {
                responseDto.setRslt_msg(jsonObject.optString("ErrorMessage"));
            }
            return responseDto;
        } catch (IOException e) {
            throw new CustomedBadRequestException(e);
        }
    }

    public ResponseDto validateVietnameseID(String id) {
        String vietnameseIdRegex = "^(\\d{12})$";
        ResponseDto responseDto = new ResponseDto();

        if (!Pattern.matches(vietnameseIdRegex, id)) {
            responseDto.setRslt_msg("Fail");
            responseDto.setReason_code("Căn cước công dân phải có đúng 12 chữ số và chỉ chứa số.");
            return responseDto;
        }

        if (id.length() == 12) {
            int centuryCode = Character.getNumericValue(id.charAt(3));
            int birthYear = Integer.parseInt(id.substring(4, 6));

            if (centuryCode == 0 || centuryCode == 1) {
                birthYear += 1900;
            } else if (centuryCode == 2 || centuryCode == 3) {
                birthYear += 2000;
            } else {
                responseDto.setRslt_msg("Fail");
                responseDto.setReason_code("Căn cước công dân không hợp lệ.");
                return responseDto;
            }

            int currentYear = Year.now().getValue();
            int age = currentYear - birthYear;

            if (age < 20 || age > 60) {
                responseDto.setRslt_msg("Fail");
                responseDto.setReason_code("Độ tuổi theo số CCCD của Quý khách không nằm trong độ tuổi được cung cấp khoản vay của LOTTE Finance");
                return responseDto;
            }
        }

        responseDto.setRslt_msg("Success");
        responseDto.setReason_code("Hợp lệ.");
        return responseDto;
    }

    public ResponseDto validateVietnamesePhoneNumber(String phoneNumber) {
        String vietnamesePhoneRegex = "^(03|05|07|08|09)\\d{8}$";
        ResponseDto responseDto = new ResponseDto();
        if (!Pattern.matches(vietnamesePhoneRegex, phoneNumber)) {
            if (!phoneNumber.matches("^\\d+$")) {
                responseDto.setRslt_msg("Fail");
                responseDto.setReason_code("Số điện thoại chỉ được chứa các ký tự số.");
                return responseDto;
            }
            if (!phoneNumber.matches("^(03|05|07|08|09).*")) {
                responseDto.setRslt_msg("Fail");
                responseDto.setReason_code("Số điện thoại phải bắt đầu bằng các đầu số hợp lệ (03, 05, 07, 08, 09).");
                return responseDto;
            }
            if (phoneNumber.length() != 10) {
                responseDto.setRslt_msg("Fail");
                responseDto.setReason_code("Số điện thoại phải có đúng 10 chữ số.");
                return responseDto;
            }
            responseDto.setRslt_msg("Fail");
            responseDto.setReason_code("Số điện thoại không hợp lệ.");
            return responseDto;
        }
        responseDto.setRslt_msg("Success");
        responseDto.setReason_code("Hợp lệ.");
        return responseDto;
    }

    public String checkPhoneExitsOnlyData(CheckPhoneExitsRequestDTO phoneNumber) {
        log.info("Start checkPhoneExitsOnlyData: {}", phoneNumber);

        ESBRequestDTO esbRequestDTO = new ESBRequestDTO();
        esbRequestDTO.setTransId(UUID.randomUUID().toString());
        esbRequestDTO.setData(phoneNumber);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequestBody;
        try {
            jsonRequestBody = objectMapper.writeValueAsString(esbRequestDTO);
        } catch (IOException e) {
            throw new CustomedBadRequestException("Error serializing DTO", e);
        }

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"), jsonRequestBody);
        String url = cardUrl + checkExitService;
        log.info("URL: {}", url);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        log.info("REQUEST : {}", request);

        try (Response response = okHttpClient.newCall(request).execute()) {
            String respone = response.body().string();
            log.info("RESPONSE : {}", respone);

            JSONObject jsonObject = new JSONObject(respone);

            JSONObject data = jsonObject.optJSONObject("Data");
            return data != null ? data.toString() : "{}";
        } catch (IOException e) {
            throw new CustomedBadRequestException("Lỗi gọi API", e);
        }
    }

    public String findFirstByBrandAndPriceAndStatus(FindFirstByBrandAndPriceAndStatusRequestDTO requestDTO) {
        log.info("Start findFirstByBrandAndPriceAndStatus: {}", requestDTO);
        ESBRequestDTO esbRequestDTO = new ESBRequestDTO();
        esbRequestDTO.setTransId(UUID.randomUUID().toString());
        esbRequestDTO.setData(requestDTO);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequestBody;
        try {
            jsonRequestBody = objectMapper.writeValueAsString(esbRequestDTO);
        } catch (IOException e) {
            throw new CustomedBadRequestException("Error serializing DTO", e);
        }

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"), jsonRequestBody);
        String url = cardUrl + findFirstStatusBrandPriceService;
        Request request = new Request.Builder().url(url).post(requestBody).build();
        log.info("URL: {}", url);
        log.info("REQUEST : {}", request);


        try (Response response = okHttpClient.newCall(request).execute()) {
            String respone = response.body().string();
            JSONObject jsonObject = new JSONObject(respone);
            log.info("RESPONSE : {}", respone);
            JSONObject data = jsonObject.optJSONObject("Data");
            return data != null ? data.toString() : "{}";
        } catch (IOException e) {
            throw new CustomedBadRequestException("Lỗi gọi API", e);
        }
    }

    public String findPhoneToken(FindPhoneAndTokenRequestDTO requestDTO) {
        log.info("Start findPhoneToken: {}", requestDTO);

        ESBRequestDTO esbRequestDTO = new ESBRequestDTO();
        esbRequestDTO.setTransId(UUID.randomUUID().toString());
        esbRequestDTO.setData(requestDTO);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequestBody;
        try {
            jsonRequestBody = objectMapper.writeValueAsString(esbRequestDTO);
        } catch (IOException e) {
            throw new CustomedBadRequestException("Error serializing DTO", e);
        }

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"), jsonRequestBody);
        String url = cardUrl + findPhoneAndTokenService;
        Request request = new Request.Builder().url(url).post(requestBody).build();
        log.info("URL: {}", url);
        log.info("REQUEST: {}", request);

        try (Response response = okHttpClient.newCall(request).execute()) {
            String respone = response.body().string();
            JSONObject jsonObject = new JSONObject(respone);
            log.info("RESPONSE: {}", respone);

            JSONObject data = jsonObject.optJSONObject("Data");
            return data != null ? data.toString() : "{}";
        } catch (IOException e) {
            throw new CustomedBadRequestException("Lỗi gọi API", e);
        }
    }

    public String upsertMobileCard(MobileCardRequestDTO requestDTO) {
        log.info("Start upsertMobileCard: {}", requestDTO);

        ESBRequestDTO esbRequestDTO = new ESBRequestDTO();
        esbRequestDTO.setTransId(UUID.randomUUID().toString());
        esbRequestDTO.setData(requestDTO);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequestBody;
        try {
            jsonRequestBody = objectMapper.writeValueAsString(esbRequestDTO);
        } catch (IOException e) {
            throw new CustomedBadRequestException("Error serializing DTO", e);
        }

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"), jsonRequestBody);
        String url = cardUrl + upsertMobileCardService;
        Request request = new Request.Builder().url(url).post(requestBody).build();
        log.info("URL: {}", url);
        log.info("REQUEST: {}", request);

        try (Response response = okHttpClient.newCall(request).execute()) {
            String respone = response.body().string();
            JSONObject jsonObject = new JSONObject(respone);
            log.info("RESPONSE: {}", respone);

            JSONObject data = jsonObject.optJSONObject("Data");
            return data != null ? data.toString() : "{}";
        } catch (IOException e) {
            throw new CustomedBadRequestException("Lỗi gọi API", e);
        }
    }

    public String upsertPhoneToken(PhoneVerifyTokenRequestDTO phoneVerifyTokenRequestDTO) {
        log.info("Start upsertPhoneToken: {}", phoneVerifyTokenRequestDTO);

        ESBRequestDTO esbRequestDTO = new ESBRequestDTO();
        esbRequestDTO.setTransId(UUID.randomUUID().toString());
        esbRequestDTO.setData(phoneVerifyTokenRequestDTO);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequestBody;
        try {
            jsonRequestBody = objectMapper.writeValueAsString(esbRequestDTO);
        } catch (IOException e) {
            throw new CustomedBadRequestException("Error serializing DTO", e);
        }

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"), jsonRequestBody);
        String url = cardUrl + upsertPhoneTokenService;
        Request request = new Request.Builder().url(url).post(requestBody).build();
        log.info("URL: {}", url);
        log.info("REQUEST: {}", request);

        try (Response response = okHttpClient.newCall(request).execute()) {
            String respone = response.body().string();
            JSONObject jsonObject = new JSONObject(respone);
            log.info("RESPONSE: {}", response);

            JSONObject data = jsonObject.optJSONObject("Data");
            return data != null ? data.toString() : "{}";
        } catch (IOException e) {
            throw new CustomedBadRequestException("Lỗi gọi API", e);
        }
    }

    public GetCardResponseDTO getActivePriceByBrandService(GetListCardActiveByBrandRequestDTO requestDTO) {
        log.info("Start upsertPhoneToken: {}", requestDTO);

        ESBRequestDTO esbRequestDTO = new ESBRequestDTO();
        esbRequestDTO.setTransId(UUID.randomUUID().toString());
        esbRequestDTO.setData(requestDTO);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequestBody;
        try {
            jsonRequestBody = objectMapper.writeValueAsString(esbRequestDTO);
        } catch (IOException e) {
            throw new CustomedBadRequestException("Error serializing DTO", e);
        }

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"), jsonRequestBody);
        String url = cardUrl + getActivePriceByBrandService;
        Request request = new Request.Builder().url(url).post(requestBody).build();
        log.info("URL: {}", url);
        log.info("REQUEST: {}", request);

        try (Response response = okHttpClient.newCall(request).execute()) {
            String respone = response.body().string();
            JSONObject jsonObject = new JSONObject(respone);
            log.info("RESPONSE: {}", response);

            JSONObject data = jsonObject.optJSONObject("Data");
            String prices = "";
            if (data != null && data.has("prices")) {
                prices = data.getString("prices");
            }

            return GetCardResponseDTO
                    .builder()
                    .prices(prices)
                    .reason_code("0")
                    .rslt_msg("Success")
                    .rslt_cd("s")
                    .build();
        } catch (IOException e) {
            throw new CustomedBadRequestException("Lỗi gọi API", e);
        }
    }

    public ResponseDto saveWarehouse(DataDto dto, String channel) {
        log.info("saveWarehouse Request: " + dto.toString());
        CustomerInfoRequestDto customerInfoRequestDto = new CustomerInfoRequestDto();
        customerInfoRequestDto.setTransId(UUID.randomUUID().toString());
        if (dto.getInviteList() != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<InviteInfo> inviteInfos = mapper.readValue(dto.getInviteList(), new TypeReference<List<InviteInfo>>() {
                });
                for (InviteInfo invite : inviteInfos) {
                    log.info("Invite: {} - {}", invite.getName(), invite.getPhone());
                }

                String inviteJson = mapper.writeValueAsString(inviteInfos);
                log.info("Converted InviteList to JSON: {}", inviteJson);

                dto.setInviteList(inviteJson);

            } catch (IOException e) {
                throw new CustomedBadRequestException("Invalid inviteList JSON", e);
            }
        }
        customerInfoRequestDto.setData(dto);
        ResponseDto responseDto = new ResponseDto();
        if (channel != ChannelEnum.LOAN7.getVal()) {
            ResponseDto phoneValidationResult = validateVietnamesePhoneNumber(dto.getPhoneNumber());
            if (!"Success".equals(phoneValidationResult.getRslt_msg())) {
                return phoneValidationResult;
            }
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequestBody;
        try {
            jsonRequestBody = objectMapper.writeValueAsString(customerInfoRequestDto);
        } catch (IOException e) {
            throw new CustomedBadRequestException("Error serializing DTO", e);
        }
        if (channel != ChannelEnum.LOAN7.getVal()) {
            String key = String.format("%s_%s", dto.getPhoneNumber(), dto.getIdCard());
            log.info("putInCache: {}", key);
            cacheService.putInCache(key, OtpVerifyStatusEnum.DONE.getVal());
        } else {
            String key = String.format("%s_%s", dto.getPhoneNumber(), channel);
            log.info("putInCache: {}", key);
            cacheService.putInCache(key, OtpVerifyStatusEnum.DONE.getVal());
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonRequestBody);
        String url = esbBaseUrl + inquiryService;
        Request request = new Request.Builder().url(url).post(requestBody).build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            String respone = response.body().string();
            JSONObject jsonObject = new JSONObject(respone);
            responseDto.setReason_code(jsonObject.optString("ErrorCode"));
            if (jsonObject.optString("ErrorMessage").equals("Successful")) {
                responseDto.setRslt_msg("Success");
                log.info("Start create cache: {}");
            } else {
                responseDto.setRslt_msg(jsonObject.optString("ErrorMessage"));
            }
            return responseDto;
        } catch (IOException e) {
            throw new CustomedBadRequestException(e);
        }
    }

}
