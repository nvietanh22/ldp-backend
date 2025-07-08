package vn.lottefinance.landingpage.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

@Slf4j
public class ProfileUtil {

    @Getter
    static private String currentProfile;

    public ProfileUtil(Environment environment) {
        for (String profile : environment.getActiveProfiles()) {
            currentProfile = profile;
            break;
        }
        log.info("Current Profile : " + currentProfile);
    }

    static private Boolean checkProfile(String profile) {
        return profile.equals(currentProfile);
    }

    static public Boolean isLocal() {
        return checkProfile(PROFILE.LOCAL.name());
    }

    public enum PROFILE {
        LOCAL,
    }
}
