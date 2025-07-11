package vn.lottefinance.landingpage.dto.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MobileCardRequestDTO {
    private Long id;
    private String link;
    private String name;
    private String sms;
    private String email;
    private String partnerCode;
    private String gotitCode;
    private String voucherSerial;
    private String brand;
    private String productName;
    private String price;
    private String issueDate;
    private String expiredDate;
    private String transactionRefId;
    private String poNumber;
    private String status;
    private String createdDate;
    private String phoneNumber;
    private String receivedDate;
}
