package vn.lottefinance.landingpage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import vn.lottefinance.landingpage.utils.CacheUtil;
import vn.lottefinance.landingpage.utils.ProfileUtil;

@Configuration
public class CommonConfig {
    @Bean
    public ProfileUtil profileUtil(Environment environment) {
        return new ProfileUtil(environment);
    }


}
