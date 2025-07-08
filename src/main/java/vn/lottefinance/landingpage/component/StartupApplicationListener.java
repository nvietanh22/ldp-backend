package vn.lottefinance.landingpage.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import vn.lottefinance.landingpage.properties.*;

@Slf4j
@Component
public class StartupApplicationListener implements
        ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    CaptchaProperties captchaProperties;

    @Autowired
    BaseProperties baseProperties;

    @Autowired
    CardProperties cardProperties;

    @Autowired
    PlccProperties plccProperties;

    @Autowired
    LoanProperties loanProperties;

    @Autowired
    PsProperties psProperties;

    @Override public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("=================Application config==================");
        log.info(captchaProperties.toString());
        log.info(baseProperties.toString());
        log.info(cardProperties.toString());
        log.info(plccProperties.toString());
        log.info(psProperties.toString());
        log.info(loanProperties.toString());
        log.info("=================------------------==================");
    }
}