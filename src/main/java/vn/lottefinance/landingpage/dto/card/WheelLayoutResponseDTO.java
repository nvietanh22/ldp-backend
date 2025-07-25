package vn.lottefinance.landingpage.dto.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WheelLayoutResponseDTO {
    private List<PrizeSegmentDTO> wheelLayout;
    private String layoutId;
    private String rslt_cd;
    private String rslt_msg;
}