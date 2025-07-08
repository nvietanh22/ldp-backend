package vn.lottefinance.landingpage.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ToString
@ConfigurationProperties(prefix = "google.recaptcha")
public class CaptchaProperties {

    private String site;
    private String secret;
    private String endpoint;
    private boolean proxy;
    private String proxyHost;
    private int proxyPort;
    private boolean enable;
}