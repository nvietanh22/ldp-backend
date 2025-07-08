package vn.lottefinance.landingpage.dto.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetMobileCardRequestDTO {
    private String brand;
    private String phoneNumber;
    private String price;
    private String token;
}
