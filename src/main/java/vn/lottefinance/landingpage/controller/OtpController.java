package vn.lottefinance.landingpage.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.lottefinance.landingpage.annotation.RequiresCaptcha;
import vn.lottefinance.landingpage.dto.otp.OtpGenDto;
import vn.lottefinance.landingpage.dto.otp.OtpVerifyDto;
import vn.lottefinance.landingpage.enums.ChannelEnum;
import vn.lottefinance.landingpage.services.ChannelService;
import vn.lottefinance.landingpage.services.OtpService;

@RestController
@RequestMapping("/api/otp")
@Validated
@Slf4j
public class OtpController {
    @Autowired
    private OtpService otpService;

    @Autowired
    private ChannelService channelService;

    @PostMapping("/gen-otp")
    @RequiresCaptcha
    @ResponseStatus(HttpStatus.OK)
    public OtpGenDto.Response genOtp(@Valid @RequestBody OtpGenDto.Request req, HttpServletRequest request) {
        return otpService.genOtp(req, channelService.processChannel(request.getHeader("referer")));
    }

    @PostMapping("/verify-otp")
    @RequiresCaptcha
    @ResponseStatus(HttpStatus.OK)
    public OtpVerifyDto.Response verifyOtp(@Valid @RequestBody OtpVerifyDto.Request req, HttpServletRequest request) {
        return otpService.verify(req, channelService.processChannel(request.getHeader("referer")));
    }

    @PostMapping("/ps/gen-otp")
    @ResponseStatus(HttpStatus.OK)
    public OtpGenDto.Response psGenOtp(@Valid @RequestBody OtpGenDto.Request req, HttpServletRequest request) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        OtpGenDto.Response response = otpService.genOtp(req, ChannelEnum.PS.getVal());
        log.info(mapper.writeValueAsString(response));
        return response;
    }

    @PostMapping("/ps/verify-otp")
    @ResponseStatus(HttpStatus.OK)
    public OtpVerifyDto.Response psVerifyOtp(@Valid @RequestBody OtpVerifyDto.Request req, HttpServletRequest request) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        OtpVerifyDto.Response response = otpService.verify(req, ChannelEnum.PS.getVal());
        log.info(mapper.writeValueAsString(response));
        return response;
    }
}
