package vn.lottefinance.landingpage.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CardEnum {

    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    LUCKY("MMLSau"),
    SUCCESS("Success");
    private String status;
}
