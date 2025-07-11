package vn.lottefinance.landingpage.dto.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckPhoneExitsRequestDTO {
    @JsonProperty("phoneNumber")
    private String phoneNumber;

}
