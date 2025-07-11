package vn.lottefinance.landingpage.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.lottefinance.landingpage.dto.card.GetMobileCardRequestDTO;
import vn.lottefinance.landingpage.dto.card.GetMobileCardResponseDTO;
import vn.lottefinance.landingpage.dto.card.ValidateRequestDTO;
import vn.lottefinance.landingpage.dto.card.ValidateResponseDTO;
import vn.lottefinance.landingpage.services.ChannelService;
import vn.lottefinance.landingpage.services.MobileCardService;

@RestController
@RequestMapping("/api/mobile-cards")
@RequiredArgsConstructor
@Slf4j
public class MobileCardController {

    private final MobileCardService service;

    private final ChannelService channelService;


    @PostMapping("/get-card")
    @ResponseStatus(HttpStatus.OK)
    public GetMobileCardResponseDTO getCardBy(@Valid @RequestBody GetMobileCardRequestDTO request) {
        return service.getCardNumber(request);
    }
//
//    @PostMapping("/create-minigame-infor")
//    @ResponseStatus(HttpStatus.OK)
//    public GetMobileCardResponseDTO saveInformationMiniGame(@Valid @RequestBody GetMobileCardRequestDTO request) {
//        return service.getCardNumber(request);
//    }

//    @PostMapping("/import")
//    public ResponseEntity<String> importFromExcel(@RequestParam("file") MultipartFile file) {
//        try {
//            service.importMobileCardsFromExcel(file);
//            return ResponseEntity.ok("Import thành công");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Import thất bại: " + e.getMessage());
//        }
//    }

    @PostMapping("/verify-phone")
    public ValidateResponseDTO sendValidate(@Valid @RequestBody ValidateRequestDTO req, HttpServletRequest request) throws JsonProcessingException {
//        /String channel = channelService.processChannel(request.getHeader("referer"));
//        log.info("referer " + request.getHeader("referer"));
//        log.info("Channel sendValidate " + channel);
        log.info("Start verify-phone: {}", req);

        return service.sendValidate(req, "LOAN");
//        return service.sendValidate(req, channel);
    }

}
