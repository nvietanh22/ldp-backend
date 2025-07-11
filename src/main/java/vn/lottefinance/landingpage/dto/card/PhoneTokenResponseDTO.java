package vn.lottefinance.landingpage.dto.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhoneTokenResponseDTO {
    private Long id;
    private String phoneNumber;
    private String token;
    private Date createdAt;
    private Date expiredAt;
    private Integer verifyed;

    private String message;
    private Integer status;

    public boolean isVerifyed() {
        return verifyed != null && verifyed == 1;
    }
}

