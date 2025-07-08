package vn.lottefinance.landingpage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerInfoRequestDto {
    @JsonProperty("TransId")
    private String TransId;
    @JsonProperty("Data")
    private DataDto Data;
}