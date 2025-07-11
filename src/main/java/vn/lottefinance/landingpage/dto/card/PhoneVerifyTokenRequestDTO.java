package vn.lottefinance.landingpage.dto.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhoneVerifyTokenRequestDTO {

    private String phoneNumber;

    private String token;

    private String createdAt;

    private String expiredAt;

    private Integer verified;
}
