package vn.lottefinance.landingpage.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.lottefinance.landingpage.enums.ChannelEnum;
import vn.lottefinance.landingpage.enums.ChannelRefEnum;

import java.util.Arrays;

@Slf4j
@Service
public class ChannelService {

    public String processChannel(String referer) {
        return Arrays.stream(ChannelRefEnum.values())
                .filter(refEnum -> referer.contains(refEnum.getCode()))
                .map(refEnum -> refEnum.getChannel().getVal())
                .findFirst()
                .orElse("");
    }
}
