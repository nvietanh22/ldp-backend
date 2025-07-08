package vn.lottefinance.landingpage.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ChannelRefEnum {
    CARD("cards.", ChannelEnum.CARD),
    CARD_UAT("cards-uat.", ChannelEnum.CARD),
    PLCC("thelottecard", ChannelEnum.PLCC),
    PLCC_UAT("thelottecard-uat", ChannelEnum.PLCC),
    PS("/partnership/", ChannelEnum.PS),
    PS_UAT("cards-uat.lottefinance.vn/partnership/", ChannelEnum.PS),
    LOAN1("/vaynhanh1", ChannelEnum.LOAN1),
    LOAN2("/vaynhanh2/", ChannelEnum.LOAN2),
    LOAN3("/vaynhanh3/", ChannelEnum.LOAN3),
    LOAN4("/vaynhanh4/", ChannelEnum.LOAN4),
    LOAN5("/vaynhanh5/", ChannelEnum.LOAN5),
    LOAN7("/vaynhanh7/", ChannelEnum.LOAN7),
    DAGORAS("/dagoras/", ChannelEnum.DAGORAS),
    LOAN("vay.lottefinance.vn", ChannelEnum.LOAN),
    LOAN_UAT("vay-uat.lottefinance.vn", ChannelEnum.LOAN),
    DAGORAS_UAT("/dagoras/", ChannelEnum.DAGORAS);


    private final String code;
    private final ChannelEnum channel;
}
