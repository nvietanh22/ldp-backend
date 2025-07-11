package vn.lottefinance.landingpage.dto.card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MobileCardResponseDTO {
    private String status;
    private String partnerCode;
    private String voucherSerial;
    private String phoneNumber;
    private Date receivedDate;
    private String gotitCode;
    private String productName;
    private String link;
    private String sms;
    private String email;
    private String brand;
    private String price;
    private String poNumber;
    private String transRefId;
    private Date issueDate;
    private Date expiredDate;
    private String createdDate;
    private String name;
    private Long id;
    private Integer statusOut;
    private String message;


}
