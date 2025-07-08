package vn.lottefinance.landingpage.dto.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MiniGameInformationResponseDTO {
    private Date createdDate;
    private String phoneNumber;
    private String otpStatus;
    private String customerType;
    private String status;
    private String carriers;
}
