package vn.lottefinance.landingpage.services;

import vn.lottefinance.landingpage.dto.otp.OtpGenDto;
import vn.lottefinance.landingpage.dto.otp.OtpVerifyDto;

public interface OtpService {
    OtpGenDto.Response genOtp(OtpGenDto.Request request, String channel);
    OtpVerifyDto.Response verify(OtpVerifyDto.Request request, String channel);
}
