package vn.lottefinance.landingpage.dto.card;

import com.google.gson.annotations.SerializedName;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidateRequestDTO {
    @SerializedName("request_id")
    private String request_id;
    @SerializedName("contact_number")
    @Pattern(regexp="^[0-9]{10,11}$", message = "Invalid contact_number")
    private String contact_number;
    @SerializedName("national_id")
    private String national_id;
}
