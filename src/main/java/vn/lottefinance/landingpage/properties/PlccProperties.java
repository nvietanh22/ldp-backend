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
@ConfigurationProperties(prefix = "base.plcc")
public class PlccProperties {
    private String sendUrl;
    private String validateUrl;
    private String channel;
}
