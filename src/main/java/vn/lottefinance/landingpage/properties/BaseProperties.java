package vn.lottefinance.landingpage.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ToString
@Component
@ConfigurationProperties(prefix = "base")
public class BaseProperties {
    private String user;
    private String pass;
    private String channel;
    private String sendUrl;
    private String validateUrl;
}