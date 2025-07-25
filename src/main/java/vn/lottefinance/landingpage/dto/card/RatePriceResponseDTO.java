package vn.lottefinance.landingpage.dto.card;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
@SuperBuilder
@NoArgsConstructor
@Data
public class RatePriceResponseDTO {
    @SerializedName("rslt_cd")
    private String rslt_cd;

    @SerializedName("rslt_msg")
    private String rslt_msg;

    @SerializedName("reason_code")
    private String reason_code;

    private List<RatePriceDTO> rate_price;
}
