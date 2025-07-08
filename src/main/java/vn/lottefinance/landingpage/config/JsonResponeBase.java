package vn.lottefinance.landingpage.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JsonResponeBase<T> {
    private T data;
    private String status;
    private String rslt_msg;
}