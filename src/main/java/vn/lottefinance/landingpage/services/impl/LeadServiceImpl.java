package vn.lottefinance.landingpage.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.util.text.TextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.lottefinance.landingpage.client.BaseClient;
import vn.lottefinance.landingpage.client.custom.*;
import vn.lottefinance.landingpage.dto.lead.LeadDto;
import vn.lottefinance.landingpage.dto.lead.LeadValidateDto;
import vn.lottefinance.landingpage.enums.ChannelEnum;
import vn.lottefinance.landingpage.enums.OtpVerifyStatusEnum;
import vn.lottefinance.landingpage.exception.CustomedBadRequestException;
import vn.lottefinance.landingpage.exception.ServiceUnavailableException;
import vn.lottefinance.landingpage.services.CacheService;
import vn.lottefinance.landingpage.services.LeadService;
import vn.lottefinance.landingpage.utils.ValidationExtendtion;

import java.io.IOException;

@Slf4j
@Service
public class LeadServiceImpl implements LeadService {

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private LoanClient loanClient;

    @Autowired
    private PlccClient plccClient;

    @Autowired
    private Loan01Client loan1Client;

    @Autowired
    private PsClient psClient;

    @Autowired
    private CardClient cardClient;

    @Autowired
    private DagorasClient dagorasClient;

    @Autowired
    private Loan02Client loan02Client;

    @Autowired
    private Loan03Client loan03Client;

    @Autowired
    private Loan04Client loan04Client;

    @Autowired
    private Loan05Client loan05Client;

    @Autowired
    private Loan07Client loan07Client;

    @Autowired
    private CacheService cacheService;

//    @Autowired
//    private TextEncryptor textEncryptor;

    @Override
    public LeadDto.Response sendLead(LeadDto.Request req, String channel) throws JsonMappingException, JsonProcessingException {
        // fixed data not use
//        if (!StringUtils.isEmpty(channel)) {
//            channel = textEncryptor.decrypt(channel);
//        }
        String key = String.format("%s_%s", channel, req.getContact_number());
        if (req != null) {
            req.setBirthday(null);
            req.setReq_period(null);
            req.setReq_amount("0");
            if (!StringUtils.isEmpty(req.getNote())) {
                validateNote(req.getNote(), channel);
            }

            String otpVerified = cacheService.getFromCache(key).orElse(null);
            if (!channel.equals(ChannelEnum.DAGORAS.getVal())) {
                if (otpVerified == null) {
                    log.error("Customer not verify otp before send");
                    throw new CustomedBadRequestException("Customer not verify otp before send");
                }

                if (otpVerified.equals(OtpVerifyStatusEnum.FAIL.getVal())) {
                    log.error("Customer not verify otp success before send");
                    throw new CustomedBadRequestException("Customer not verify otp success before send");
                }
            }
        }
        log.info("Channel: " + (StringUtils.isEmpty(channel) ? ChannelEnum.CARD.getVal() : channel));
        log.info("Request: " + req.toString());
        if (StringUtils.isEmpty(channel)) {
            LeadDto.Response response = (LeadDto.Response) cardClient.send(req, LeadDto.Response.class);

            return response;
        }

        if (channel.equals(ChannelEnum.CARD.getVal())) {
            LeadDto.Response response = (LeadDto.Response) cardClient.send(req, LeadDto.Response.class);
            cacheService.deleteCache(key);
            return response;
        }

        if (channel.equals(ChannelEnum.PLCC.getVal())) {
            LeadDto.Response response = (LeadDto.Response) plccClient.send(req, LeadDto.Response.class);
            cacheService.deleteCache(key);
            return response;
        }

        if (channel.equals(ChannelEnum.PS.getVal())) {
            LeadDto.Response response = (LeadDto.Response) psClient.send(req, LeadDto.Response.class);
            cacheService.deleteCache(key);
            return response;
        }

        if (channel.equals(ChannelEnum.LOAN.getVal())) {
            LeadDto.Response response = (LeadDto.Response) loanClient.send(req, LeadDto.Response.class);
            cacheService.deleteCache(key);
            return response;
        }

        if (channel.equals(ChannelEnum.LOAN1.getVal())) {
            LeadDto.Response response = (LeadDto.Response) loan1Client.send(req, LeadDto.Response.class);
            cacheService.deleteCache(key);
            return response;
        }

        if (channel.equals(ChannelEnum.LOAN2.getVal())) {
            LeadDto.Response response = (LeadDto.Response) loan02Client.send(req, LeadDto.Response.class);
            cacheService.deleteCache(key);
            return response;
        }

        if (channel.equals(ChannelEnum.LOAN3.getVal())) {
            LeadDto.Response response = (LeadDto.Response) loan03Client.send(req, LeadDto.Response.class);
            cacheService.deleteCache(key);
            return response;
        }

        if (channel.equals(ChannelEnum.LOAN4.getVal())) {
            LeadDto.Response response = (LeadDto.Response) loan04Client.send(req, LeadDto.Response.class);
            cacheService.deleteCache(key);
            return response;
        }

        if (channel.equals(ChannelEnum.LOAN5.getVal())) {
            LeadDto.Response response = (LeadDto.Response) loan05Client.send(req, LeadDto.Response.class);
            cacheService.deleteCache(key);
            return response;
        }

        if (channel.equals(ChannelEnum.LOAN7.getVal())) {
            LeadDto.Response response = (LeadDto.Response) loan07Client.send(req, LeadDto.Response.class);
            cacheService.deleteCache(key);
            return response;
        }

        if (channel.equals(ChannelEnum.DAGORAS.getVal())) {
            LeadDto.Response response = (LeadDto.Response) dagorasClient.send(req, LeadDto.Response.class);
            cacheService.deleteCache(key);
            return response;
        }

        log.error("Not found channel");
        throw new CustomedBadRequestException("Not found channel");
    }

    @Override
    public LeadValidateDto.Response sendValidate(LeadValidateDto.Request req, String channel) throws JsonMappingException, JsonProcessingException {
//        channel = (StringUtils.isEmpty(channel) ? ChannelEnum.CARD.getVal() : textEncryptor.decrypt(channel));
        log.info("Channel: " + channel);
        log.info("Request: " + req.toString());
        if (StringUtils.isEmpty(channel)) {
            LeadValidateDto.Response response = (LeadValidateDto.Response) cardClient.sendValidate(req, LeadValidateDto.Response.class);
            return response;
        }
        if (channel.equals(ChannelEnum.CARD.getVal())) {
            LeadValidateDto.Response response = (LeadValidateDto.Response) cardClient.sendValidate(req, LeadValidateDto.Response.class);
            return response;
        }

        if (channel.equals(ChannelEnum.PLCC.getVal())) {
            LeadValidateDto.Response response = (LeadValidateDto.Response) plccClient.sendValidate(req, LeadValidateDto.Response.class);
            return response;
        }

        if (channel.equals(ChannelEnum.PS.getVal())) {
            LeadValidateDto.Response response = (LeadValidateDto.Response) psClient.sendValidate(req, LeadValidateDto.Response.class);
            return response;
        }

        if (channel.equals(ChannelEnum.LOAN.getVal())) {
            LeadValidateDto.Response response = (LeadValidateDto.Response) loanClient.sendValidate(req, LeadValidateDto.Response.class);
            return response;
        }

        if (channel.equals(ChannelEnum.LOAN1.getVal())) {
            LeadValidateDto.Response response = (LeadValidateDto.Response) loan1Client.sendValidate(req, LeadValidateDto.Response.class);
            return response;
        }

        if (channel.equals(ChannelEnum.LOAN2.getVal())) {
            LeadValidateDto.Response response = (LeadValidateDto.Response) loan02Client.sendValidate(req, LeadValidateDto.Response.class);
            return response;
        }

        if (channel.equals(ChannelEnum.LOAN3.getVal())) {
            LeadValidateDto.Response response = (LeadValidateDto.Response) loan03Client.sendValidate(req, LeadValidateDto.Response.class);
            return response;
        }

        if (channel.equals(ChannelEnum.LOAN4.getVal())) {
            LeadValidateDto.Response response = (LeadValidateDto.Response) loan04Client.sendValidate(req, LeadValidateDto.Response.class);
            return response;
        }

        if (channel.equals(ChannelEnum.LOAN5.getVal())) {
            LeadValidateDto.Response response = (LeadValidateDto.Response) loan05Client.sendValidate(req, LeadValidateDto.Response.class);
            return response;
        }

        if (channel.equals(ChannelEnum.LOAN7.getVal())) {
            LeadValidateDto.Response response = (LeadValidateDto.Response) loan07Client.sendValidate(req, LeadValidateDto.Response.class);
            return response;
        }

        if (channel.equals(ChannelEnum.DAGORAS.getVal())) {
            LeadValidateDto.Response response = (LeadValidateDto.Response) dagorasClient.sendValidate(req, LeadValidateDto.Response.class);
            return response;
        }

        log.error("Not found channel");
        throw new CustomedBadRequestException("Not found channel");
    }

    private void validateNote(String val, String channel) throws JsonMappingException, JsonProcessingException {
        try {
            log.info("Start validate note");
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(val);
            log.info(jsonNode.toPrettyString());
            if (jsonNode.has("i_agree_terms_and_conditions")) {
                if (!jsonNode.get("i_agree_terms_and_conditions").isBoolean()) {
                    throw new CustomedBadRequestException("i_agree_terms_and_conditions is bollen");
                }
            }

            if (!channel.equals(ChannelEnum.PLCC.getVal())) {
                if (jsonNode.has("email")) {
                    if (!jsonNode.get("email").isNull()) {
                        if (!ValidationExtendtion.isValidEmail(jsonNode.get("email").asText())) {
                            throw new CustomedBadRequestException("email is not valid");
                        }
                    }

                } else {
                    throw new CustomedBadRequestException("email is required");
                }
            }
            if (jsonNode.has("phone")) {
                if (!jsonNode.get("phone").isTextual()) {
                    throw new CustomedBadRequestException("phone is text");
                }

                String phone = jsonNode.get("phone").asText();
                if (!ValidationExtendtion.isValidPhone(phone)) {
                    throw new CustomedBadRequestException("phone is not valid");
                }
            }
            if (!channel.equals(ChannelEnum.PLCC.getVal())) {
                if (!jsonNode.has("score")) {
                    throw new CustomedBadRequestException("score is required");
                }
            }
            if (!StringUtils.isEmpty(channel) && !channel.equals(ChannelEnum.LOAN.getVal())
                    && !channel.equals(ChannelEnum.LOAN1.getVal())
                    && !channel.equals(ChannelEnum.LOAN2.getVal())
                    && !channel.equals(ChannelEnum.LOAN3.getVal())
                    && !channel.equals(ChannelEnum.LOAN4.getVal())
                    && !channel.equals(ChannelEnum.LOAN5.getVal())
                    && !channel.equals(ChannelEnum.LOAN7.getVal())
            ) {
                if (!jsonNode.has("province")) {
                    throw new CustomedBadRequestException("province is required");
                }
            }
            if (!jsonNode.has("income_amount")) {
                throw new CustomedBadRequestException("income_amount is required");
            }
        } catch (IOException ex) {
            log.error(ex.getMessage());
            throw new ServiceUnavailableException("Lỗi hệ thống");
        }
    }

}
