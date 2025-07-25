package vn.lottefinance.landingpage.dto.card;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendNotiRequestDTO {
    @SerializedName("Phone")
    private String phone;

    @SerializedName("Channel")
    private String channel;

    @SerializedName("Code")
    private String code;

    @SerializedName("Param")
    private SendNoticeDTO param;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SendNoticeDTO {
        @SerializedName("OTP")
        private String otp;
    }
}


