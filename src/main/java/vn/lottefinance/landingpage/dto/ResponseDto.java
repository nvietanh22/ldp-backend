package vn.lottefinance.landingpage.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
public class ResponseDto {

    private String rslt_msg;

    private String reason_code;
}
