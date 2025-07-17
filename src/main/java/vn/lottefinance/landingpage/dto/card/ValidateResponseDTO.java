package vn.lottefinance.landingpage.dto.card;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
public class ValidateResponseDTO {
    @SerializedName("request_id")
    private String request_id;

    @SerializedName("rslt_cd")
    private String rslt_cd;

    @SerializedName("rslt_msg")
    private String rslt_msg;

    @SerializedName("reason_code")
    private String reason_code;

    @SerializedName("token")
    private String token;

    @SerializedName("status")
    private int status; // 0 khách hàng cũ, 1 KH mới
}
