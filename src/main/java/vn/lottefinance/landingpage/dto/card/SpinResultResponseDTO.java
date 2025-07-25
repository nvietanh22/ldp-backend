package vn.lottefinance.landingpage.dto.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpinResultResponseDTO {
    private String prize;
    private int targetIndex;
    private String rslt_msg;
    private String reason_code;
    private String rslt_cd;
}