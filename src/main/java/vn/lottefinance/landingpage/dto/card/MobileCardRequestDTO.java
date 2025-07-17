package vn.lottefinance.landingpage.dto.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.lottefinance.landingpage.utils.ExcelColumn;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MobileCardRequestDTO {

    @ExcelColumn("Link")
    private String link;

    @ExcelColumn("Name")
    private String name;

    @ExcelColumn("SMS")
    private String sms;

    @ExcelColumn("Email")
    private String email;

    @ExcelColumn("Partner Code")
    private String partnerCode;

    @ExcelColumn("Gotit Code")
    private String gotitCode;

    @ExcelColumn("Voucher Serial")
    private String voucherSerial;

    @ExcelColumn("Brand")
    private String brand;

    @ExcelColumn("Product Name")
    private String productName;

    @ExcelColumn("Price")
    private String price;

    @ExcelColumn("Issue Date")
    private String issueDate;

    @ExcelColumn("Expired Date")
    private String expiredDate;

    @ExcelColumn("Transaction Ref ID")
    private String transactionRefId;

    @ExcelColumn("PO Number")
    private String poNumber;

    // Những cột còn lại không có trong Excel, không cần mapping
    private String status;
    private String createdDate;
    private String phoneNumber;
    private String receivedDate;
}
