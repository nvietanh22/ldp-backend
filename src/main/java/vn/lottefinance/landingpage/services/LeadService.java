package vn.lottefinance.landingpage.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import vn.lottefinance.landingpage.dto.lead.LeadDto;
import vn.lottefinance.landingpage.dto.lead.LeadValidateDto;

public interface LeadService {
    LeadDto.Response sendLead(LeadDto.Request req, String channel) throws JsonMappingException, JsonProcessingException ;
    LeadValidateDto.Response sendValidate(LeadValidateDto.Request req, String channel) throws JsonMappingException, JsonProcessingException ;
}
