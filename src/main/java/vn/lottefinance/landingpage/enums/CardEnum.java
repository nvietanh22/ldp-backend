package vn.lottefinance.landingpage.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CardEnum {

    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");
    private String status;
}
