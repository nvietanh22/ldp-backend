package vn.lottefinance.landingpage.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import vn.lottefinance.landingpage.exception.ResourceForbiddenException;
import vn.lottefinance.landingpage.properties.BaseProperties;
import vn.lottefinance.landingpage.properties.CaptchaProperties;
import vn.lottefinance.landingpage.services.CaptchaService;
import vn.lottefinance.landingpage.utils.ProfileUtil;

//lombok annotation
@Slf4j
@Aspect
@Component
public class CaptchaAop {

    @Autowired
    CaptchaService service;

    @Autowired
    private HttpServletRequest httpServletRequest;

//    @Value("#{new Boolean('${app.google-capcha.enable}')}")
//    private Boolean enableGgCapcha;
    @Autowired
    private CaptchaProperties captchaProperties;
    @Around("@annotation(vn.lottefinance.landingpage.annotation.RequiresCaptcha)")
    public Object validateCaptchaResponse(final ProceedingJoinPoint point)
            throws Throwable {

        if (captchaProperties.isEnable()) {
            final HttpServletRequest request =
                    ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            final String captchaResponse = request.getHeader("captcha-response");
            final boolean isValidCaptcha = service.isValidateCaptcha(captchaResponse, httpServletRequest.getRemoteHost());
            if (!isValidCaptcha) {
                log.info("Throwing forbidden exception as the captcha is invalid.");
                throw new ResourceForbiddenException("INVALID_CAPTCHA");
            }
            return point.proceed();
        }

        return point.proceed();
    }
}
