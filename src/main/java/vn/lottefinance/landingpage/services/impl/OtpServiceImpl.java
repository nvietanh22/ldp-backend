package vn.lottefinance.landingpage.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.lottefinance.landingpage.client.custom.EsbClient;
import vn.lottefinance.landingpage.dto.otp.OtpGenDto;
import vn.lottefinance.landingpage.dto.otp.OtpVerifyDto;
import vn.lottefinance.landingpage.enums.ChannelEnum;
import vn.lottefinance.landingpage.enums.OtpVerifyStatusEnum;
import vn.lottefinance.landingpage.exception.CustomedBadRequestException;
import vn.lottefinance.landingpage.properties.*;
import vn.lottefinance.landingpage.services.CacheService;
import vn.lottefinance.landingpage.services.OtpService;
import vn.lottefinance.landingpage.utils.CacheUtil;

import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class OtpServiceImpl implements OtpService {
    @Autowired
    private EsbClient esbClient;

    @Autowired
    private CardProperties cardProperties;

    @Autowired
    private PlccProperties plccProperties;

    @Autowired
    private LoanProperties loanProperties;

    @Autowired
    private PsProperties psProperties;

    @Autowired
    private Loan1Properties loan1Properties;

    @Autowired
    private Loan2Properties loan2Properties;

    @Autowired
    private Loan3Properties loan3Properties;

    @Autowired
    private Loan4Properties loan4Properties;

    @Autowired
    private Loan5Properties loan5Properties;

    @Autowired
    private Loan7Properties loan7Properties;

    @Autowired
    private DagorasProperties dagorasProperties;

    @Autowired
    private CacheService cacheService;

    @Override
    public OtpGenDto.Response genOtp(OtpGenDto.Request request, String channel) {
        log.info("GenOtp Request: " + request.toString());
        String key = channel != ChannelEnum.LOAN7.getVal()
                ? String.format("%s_%s", request.getData().getPhone(), request.getData().getIdCard())
                : String.format("%s_%s", request.getData().getPhone(), channel);

        String otpVerified = cacheService.getFromCache(key).orElse(null);

        Set<String> validChannels = Set.of(
                ChannelEnum.CARD.getVal(),
                ChannelEnum.PLCC.getVal(),
                ChannelEnum.DAGORAS.getVal(),
                ChannelEnum.PS.getVal()
        );

        if (!validChannels.contains(channel)) {
            if (otpVerified == null) {
                log.error("Thông tin không trùng khớp với thông tin đã đăng ký");
                OtpGenDto.Response response =  OtpGenDto.Response.builder()
                        .TransId(UUID.randomUUID().toString())
                        .Data(OtpGenDto.DataResponse.builder()
                                .result(OtpGenDto.ResultResponse.builder()
                                        .status(false)
                                        .value(false)
                                        .build())
                                .build())
                        .ErrorMessage("Thông tin không trùng khớp với thông tin đã đăng ký")
                        .ErrorCode("1").build();
                return response;
            }
        }
        log.info("GenOtp Channel: " + channel);
        if (StringUtils.isEmpty(channel)) {
            request.getData().setChannel(cardProperties.getChannel());
        } else if (channel.equals(ChannelEnum.CARD.getVal())) {
            request.getData().setChannel(cardProperties.getChannel());
        } else if (channel.equals(ChannelEnum.LOAN.getVal())) {
            request.getData().setChannel(loanProperties.getChannel());
        } else if (channel.equals(ChannelEnum.PLCC.getVal())){
            request.getData().setChannel(plccProperties.getChannel());
        } else if (channel.equals(ChannelEnum.PS.getVal())){
            request.getData().setChannel(psProperties.getChannel());
        } else if (channel.equals(ChannelEnum.LOAN1.getVal())) {
            request.getData().setChannel(loan1Properties.getChannel());
        } else if (channel.equals(ChannelEnum.DAGORAS.getVal())) {
            request.getData().setChannel(dagorasProperties.getChannel());
        } else if (channel.equals(ChannelEnum.LOAN2.getVal())) {
            request.getData().setChannel(loan2Properties.getChannel());
        } else if (channel.equals(ChannelEnum.LOAN3.getVal())) {
            request.getData().setChannel(loan3Properties.getChannel());
        } else if (channel.equals(ChannelEnum.LOAN4.getVal())) {
            request.getData().setChannel(loan4Properties.getChannel());
        } else if (channel.equals(ChannelEnum.LOAN5.getVal())) {
            request.getData().setChannel(loan5Properties.getChannel());
        } else if (channel.equals(ChannelEnum.LOAN7.getVal())) {
            request.getData().setChannel(loan7Properties.getChannel());
        } else {
            OtpGenDto.Response response =  OtpGenDto.Response.builder()
                    .TransId(UUID.randomUUID().toString())
                    .Data(OtpGenDto.DataResponse.builder()
                            .result(OtpGenDto.ResultResponse.builder()
                                    .status(false)
                                    .value(false)
                                    .build())
                            .build())
                    .ErrorMessage("Not found channel")
                    .ErrorCode("1").build();
            return response;
        }

        Integer cacheOtpCnt = (Integer) CacheUtil.getCache(String.format("%s_%s", channel, request.getData().getPhone()));
        int cnt = 1;
        if (cacheOtpCnt == null) {
            CacheUtil.pushCache(String.format("%s_%s", channel, request.getData().getPhone()), cnt);
        } else {
            cnt = cacheOtpCnt;
            if (cnt >= 3) {
                OtpGenDto.Response response =  OtpGenDto.Response.builder()
                        .TransId(UUID.randomUUID().toString())
                        .Data(OtpGenDto.DataResponse.builder()
                                .result(OtpGenDto.ResultResponse.builder()
                                        .status(false)
                                        .value(false)
                                        .build())
                                .build())
                        .ErrorMessage("Số điện thoại của Quý khách đã nhận mã OTP quá 03 lần, Vui lòng gọi Hotline 1900 633 070 để được hỗ trợ")
                        .ErrorCode("1").build();
                return response;
            } else {
                CacheUtil.updateCache(String.format("%s_%s", channel, request.getData().getPhone()), ++cnt);
            }
        }
        log.info("cache: " + cacheOtpCnt);

        OtpGenDto.Response response = (OtpGenDto.Response) esbClient.send(request, "gen-otp", OtpGenDto.Response.class);
        cacheService.deleteCache(key);
        return response;
    }

    @Override
    public OtpVerifyDto.Response verify(OtpVerifyDto.Request request, String channel) {
        log.info("Verify OTP Request: " + request.toString());
        log.info("Verify OTP Channel: " + channel);
//        channel = StringUtils.isEmpty(channel) ? channel : encryptor.decrypt(channel);
        if (StringUtils.isEmpty(channel)) {
            request.getData().setChannel(cardProperties.getChannel());
        } else if (channel.equals(ChannelEnum.CARD.getVal())) {
            request.getData().setChannel(cardProperties.getChannel());
        } else if (channel.equals(ChannelEnum.LOAN.getVal())) {
            request.getData().setChannel(loanProperties.getChannel());
        } else if (channel.equals(ChannelEnum.PLCC.getVal())){
            request.getData().setChannel(plccProperties.getChannel());
        } else if (channel.equals(ChannelEnum.PS.getVal())){
            request.getData().setChannel(psProperties.getChannel());
        } else if (channel.equals(ChannelEnum.LOAN1.getVal())) {
            request.getData().setChannel(loan1Properties.getChannel());
        } else if (channel.equals(ChannelEnum.DAGORAS.getVal())) {
            request.getData().setChannel(dagorasProperties.getChannel());
        } else if (channel.equals(ChannelEnum.LOAN2.getVal())) {
            request.getData().setChannel(loan2Properties.getChannel());
        } else if (channel.equals(ChannelEnum.LOAN3.getVal())) {
            request.getData().setChannel(loan3Properties.getChannel());
        } else if (channel.equals(ChannelEnum.LOAN4.getVal())) {
            request.getData().setChannel(loan4Properties.getChannel());
        } else if (channel.equals(ChannelEnum.LOAN5.getVal())) {
            request.getData().setChannel(loan5Properties.getChannel());
        } else if (channel.equals(ChannelEnum.LOAN7.getVal())) {
            request.getData().setChannel(loan7Properties.getChannel());
        } else {
            OtpVerifyDto.Response response =  OtpVerifyDto.Response.builder()
                    .TransId(UUID.randomUUID().toString())
                    .Data(OtpVerifyDto.DataResponse.builder()
                            .result(OtpVerifyDto.ResultResponse.builder()
                                    .status(false)
                                    .value(false)
                                    .authentication("REJECT")
                                    .build())
                            .build())
                    .ErrorMessage("Not found channel")
                    .ErrorCode("1").build();
            return response;
        }

        OtpVerifyDto.Response response = (OtpVerifyDto.Response) esbClient.send(request, "verify-otp", OtpVerifyDto.Response.class);
        if (response != null) {
            if (response.getData().getResult().getAuthentication().equals("ACCEPT")) {
                String key = String.format("%s_%s", channel, request.getData().getPhone());
                cacheService.putInCache(key, OtpVerifyStatusEnum.DONE.getVal());
            } else {
                String key = String.format("%s_%s", channel, request.getData().getPhone());
                cacheService.putInCache(key, OtpVerifyStatusEnum.FAIL.getVal());
            }
        } else {
            String key = String.format("%s_%s", channel, request.getData().getPhone());
            cacheService.putInCache(key, OtpVerifyStatusEnum.FAIL.getVal());
        }
        return response;
    }
}
