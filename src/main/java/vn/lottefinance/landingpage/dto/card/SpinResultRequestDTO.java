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
public class SpinResultRequestDTO {
    private String phoneNumber;
    private String brand;
    private String token;
    private List<PrizeSegmentDTO> wheelLayout;
}