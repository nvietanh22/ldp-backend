package vn.lottefinance.landingpage.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "PHONE_VERIFICATION_TOKEN")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhoneVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PHONE_NUMBER", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "TOKEN", nullable = false, length = 100, unique = true)
    private String token;

    @Column(name = "CREATED_AT", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "EXPIRED_AT", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiredAt;

    @Column(name = "VERIFIED", nullable = false)
    private boolean verified = false;
}
