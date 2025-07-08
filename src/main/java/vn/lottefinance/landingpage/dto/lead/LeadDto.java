package vn.lottefinance.landingpage.dto.lead;

import org.hibernate.validator.constraints.Length;

import com.google.gson.annotations.SerializedName;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

public class LeadDto {

    @SuperBuilder
    @NoArgsConstructor
    @Data
    public static class Request  {
        @NotBlank
        @Length(max = 300)
        @SerializedName("request_id")
        private String request_id;

        @NotBlank
        @Length(max = 300)
        @SerializedName("device")
        private String device;

        @NotBlank
        @Length(min = 1, max = 300)
        @SerializedName("fullname")
        private String fullname;

        @SerializedName("birthday")
        private String birthday;

        @Length(max = 300)
        @SerializedName("req_amount")
        private String req_amount;

        @SerializedName("req_period")
        private String req_period;

        @SerializedName("reason")
        private String reason;

        @Pattern(regexp="^[0-9]{10,11}$", message = "Invalid contact_number")
        @NotBlank
        @SerializedName("contact_number")
        private String contact_number;

        @SerializedName("note")
        private String note;
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
    }
}



