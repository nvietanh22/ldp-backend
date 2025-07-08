package vn.lottefinance.landingpage.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import vn.lottefinance.landingpage.client.custom.ProxyCustomizer;
import vn.lottefinance.landingpage.dto.captcha.CaptchaDto;
import vn.lottefinance.landingpage.properties.CaptchaProperties;

import java.util.Objects;

@Slf4j
@Service
public class CaptchaService {

    @Autowired
    private CaptchaProperties captchaSettings;

    public boolean isValidateCaptcha(final String captchaResponse, final String remoteip) {
        log.info("Going to validate the captcha response = {}", captchaResponse);
        RestTemplate template = captchaSettings.isProxy() ? new RestTemplateBuilder(new ProxyCustomizer(captchaSettings.getProxyHost(), captchaSettings.getProxyPort())).build() : new RestTemplateBuilder().build();
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", captchaSettings.getSecret());
        params.add("response", captchaResponse);
        params.add("remoteip", remoteip);

        CaptchaDto.Response apiResponse = null;
        try {
            apiResponse = template.postForObject(captchaSettings.getEndpoint(), params, CaptchaDto.Response.class);
        } catch (final RestClientException e) {
            log.error("Some exception occurred while binding to the recaptcha endpoint.", e);
        }

        if (Objects.nonNull(apiResponse) && apiResponse.isSuccess()) {
            log.info("Captcha API response = {}", apiResponse.toString());
            return true;
        } else {
            return false;
        }
    }
}
