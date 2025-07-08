package vn.lottefinance.landingpage.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.lottefinance.landingpage.annotation.RequiresCaptcha;
import vn.lottefinance.landingpage.dto.lead.LeadDto;
import vn.lottefinance.landingpage.dto.lead.LeadValidateDto;
import vn.lottefinance.landingpage.enums.ChannelEnum;
import vn.lottefinance.landingpage.services.ChannelService;
import vn.lottefinance.landingpage.services.LeadService;

@RestController
@RequestMapping("/api/lead")
@Validated
@Slf4j
public class LeadController {

    @Autowired
    private LeadService leadService;

    @Autowired
    private ChannelService channelService;

    @PostMapping("/send")
    @RequiresCaptcha
    @ResponseStatus(HttpStatus.OK)
    public LeadDto.Response sendLead(@Valid @RequestBody LeadDto.Request req, HttpServletRequest request) throws JsonMappingException, JsonProcessingException {
        String channel = channelService.processChannel(request.getHeader("referer"));
        log.info("referer " + request.getHeader("referer"));
        log.info("Channel sendLead " + channel);
        return leadService.sendLead(req, channel);
    }

    @PostMapping("/validate")
    @RequiresCaptcha
    @ResponseStatus(HttpStatus.OK)
    public LeadValidateDto.Response sendValidate(@Valid @RequestBody LeadValidateDto.Request req, HttpServletRequest request) throws JsonMappingException, JsonProcessingException {
        String channel = channelService.processChannel(request.getHeader("referer"));
        log.info("referer " + request.getHeader("referer"));
        log.info("Channel sendValidate " + channel);
        return leadService.sendValidate(req, channel);
    }

    @PostMapping("/ps/send")
    @ResponseStatus(HttpStatus.OK)
    public LeadDto.Response psSendLead(@Valid @RequestBody LeadDto.Request req, HttpServletRequest request) throws JsonMappingException, JsonProcessingException {
        return leadService.sendLead(req, ChannelEnum.PS.getVal());
    }

    @PostMapping("/ps/validate")
    @ResponseStatus(HttpStatus.OK)
    public LeadValidateDto.Response psSendValidate(@Valid @RequestBody LeadValidateDto.Request req, HttpServletRequest request) throws JsonMappingException, JsonProcessingException {
        return leadService.sendValidate(req, ChannelEnum.PS.getVal());
    }

}
