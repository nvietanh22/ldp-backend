package vn.lottefinance.landingpage.dto.otp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

public class OtpGenDto {
    @SuperBuilder
    @NoArgsConstructor
    @Data
    public static class Request  {
        @JsonProperty("TransId")
        private String TransId;
        @JsonProperty("Data")
        private DataRequest Data;
    }

    @SuperBuilder
    @NoArgsConstructor
    @Data
    public static class DataRequest  {
        @JsonProperty("phone")
        private String phone;
        @JsonProperty("idCard")
        private String idCard;
        @JsonProperty("channel")
        private String channel;
    }

    @SuperBuilder
    @NoArgsConstructor
    @Data
    public static class Response  {
        @SerializedName("TransId")
        private String TransId;
        @SerializedName("Data")
        private DataResponse Data;
        @SerializedName("ErrorCode")
        private String ErrorCode;
        @SerializedName("ErrorMessage")
        private String ErrorMessage;
    }

    @SuperBuilder
    @NoArgsConstructor
    @Data
    public static class DataResponse  {
        @SerializedName("result")
        private ResultResponse result;
        @SerializedName("otp")
        private String otp;
    }

    @SuperBuilder
    @NoArgsConstructor
    @Data
    public static class ResultResponse  {
        @SerializedName("status")
        private Boolean status;
        @SerializedName("value")
        private Boolean value;
    }
}
