package vn.lottefinance.landingpage.dto.card;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
public class RatePriceDTO {
    @SerializedName("price")
    private String price;
    @SerializedName("rate")
    private String rate;
}
