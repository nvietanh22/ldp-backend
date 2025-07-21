package vn.lottefinance.landingpage.dto.card;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
public class GetCardResponseDTO {
    @SerializedName("rslt_cd")
    private String rslt_cd;

    @SerializedName("rslt_msg")
    private String rslt_msg;

    @SerializedName("reason_code")
    private String reason_code;

    @SerializedName("prices")
    private String prices;
}
