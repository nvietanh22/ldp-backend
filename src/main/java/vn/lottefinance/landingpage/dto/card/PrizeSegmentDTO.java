package vn.lottefinance.landingpage.dto.card;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PrizeSegmentDTO {
    private String value;
    private String name;
    private int index;
}