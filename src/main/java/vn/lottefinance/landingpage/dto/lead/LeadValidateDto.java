package vn.lottefinance.landingpage.dto.lead;


import com.google.gson.annotations.SerializedName;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

public class LeadValidateDto {
    @SuperBuilder
    @NoArgsConstructor
    @Data
    public static class Request  {
        @SerializedName("request_id")
        private String request_id;
        @SerializedName("contact_number")
        @Pattern(regexp="^[0-9]{10,11}$", message = "Invalid contact_number")
        private String contact_number;
        @SerializedName("national_id")
        private String national_id;
    }

    @SuperBuilder
    @NoArgsConstructor
    @Data
    public static class Response  {
        @SerializedName("request_id")
        private String request_id;

        @SerializedName("rslt_cd")
        private String rslt_cd;

        @SerializedName("rslt_msg")
        private String rslt_msg;

        @SerializedName("reason_code")
        private String reason_code;
    }
}
