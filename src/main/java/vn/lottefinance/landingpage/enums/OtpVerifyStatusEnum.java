package vn.lottefinance.landingpage.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OtpVerifyStatusEnum {
    DONE("DONE"),
    FAIL("FAIL");
    private String val;
}
