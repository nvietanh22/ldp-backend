package vn.lottefinance.landingpage.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.lottefinance.landingpage.utils.ExcelColumn;

import java.util.Date;

@Entity
@Table(name = "MOBILE_CARD")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MobileCard {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "MOBILE_CARD_SEQ")
    @SequenceGenerator(name = "MOBILE_CARD_SEQ", sequenceName = "MOBILE_CARD_SEQ", allocationSize = 1)
    private Long id;

    @ExcelColumn("Link")
    private String link;

    @ExcelColumn("Name")
    private String name;

    @ExcelColumn("SMS")
    private String sms;

    @ExcelColumn("Email")
    private String email;

    @ExcelColumn("Partner Code")
    @Column(name = "PARTNER_CODE")
    private String partnerCode;

    @ExcelColumn("Gotit Code")
    @Column(name = "GOTIT_CODE")
    private String gotitCode;

    @ExcelColumn("Voucher Serial")
    @Column(name = "VOUCHER_SERIAL")
    private String voucherSerial;

    @ExcelColumn("Brand")
    @Column(name = "BRAND")
    private String brand;

    @ExcelColumn("Product Name")
    @Column(name = "PRODUCT_NAME")
    private String productName;

    @ExcelColumn("Price")
    @Column(name = "PRICE")
    private String price;

    @ExcelColumn("Issue Date")
    @Column(name = "ISSUE_DATE")
    @Temporal(TemporalType.DATE)
    private Date issueDate;

    @ExcelColumn("Expired Date")
    @Column(name = "EXPIRED_DATE")
    @Temporal(TemporalType.DATE)
    private Date expiredDate;

    @ExcelColumn("Transaction Ref ID")
    @Column(name = "TRANSACTION_REF_ID")
    private String transactionRefId;

    @ExcelColumn("PO Number")
    @Column(name = "PO_NUMBER")
    private String poNumber;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "CREATED_DATE")
    private String createdDate;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "RECEIVED_DATE")
    private Date receivedDate;
}
