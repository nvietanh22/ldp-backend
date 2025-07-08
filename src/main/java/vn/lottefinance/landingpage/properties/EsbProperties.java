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
@ConfigurationProperties(prefix = "esb")
public class EsbProperties {
    private String url;
    private String user;
    private String pass;
}
