package vn.lottefinance.landingpage.dto.captcha;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

public class CaptchaDto {

    @Data
    @SuperBuilder
    @NoArgsConstructor
    public static class Request {
        @NotBlank
        private String token;

    }
    @Data
    @SuperBuilder
    @NoArgsConstructor
    public static class Response {
        private boolean success;
        @JsonProperty("challenge_ts")
        private LocalDateTime challengeTs;
        private String hostname;
        @JsonProperty("error-codes")
        private List<String> errorCodes;

    }
}

