package vn.lottefinance.landingpage.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.lottefinance.landingpage.domain.MobileCard;
import vn.lottefinance.landingpage.dto.card.GetMobileCardRequestDTO;
import vn.lottefinance.landingpage.dto.card.GetMobileCardResponseDTO;
import vn.lottefinance.landingpage.dto.card.ValidateRequestDTO;
import vn.lottefinance.landingpage.dto.card.ValidateResponseDTO;
import vn.lottefinance.landingpage.services.ChannelService;
import vn.lottefinance.landingpage.services.MobileCardService;

import java.util.List;

@RestController
@RequestMapping("/api/mobile-cards")
@RequiredArgsConstructor
@Slf4j
public class MobileCardController {

    private final MobileCardService service;

    private final ChannelService channelService;

    @GetMapping
    public ResponseEntity<List<MobileCard>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MobileCard> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<MobileCard> create(@RequestBody MobileCard mobileCard) {
        return ResponseEntity.ok(service.create(mobileCard));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/get-card")
    @ResponseStatus(HttpStatus.OK)
    public GetMobileCardResponseDTO getCardBy(@Valid @RequestBody GetMobileCardRequestDTO request) {
        return service.getCardNumber(request);
    }

    @PostMapping("/create-minigame-infor")
    @ResponseStatus(HttpStatus.OK)
    public GetMobileCardResponseDTO saveInformationMiniGame(@Valid @RequestBody GetMobileCardRequestDTO request) {
        return service.getCardNumber(request);
    }

    @PostMapping("/import")
    public ResponseEntity<String> importFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            service.importMobileCardsFromExcel(file);
            return ResponseEntity.ok("Import thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Import thất bại: " + e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ValidateResponseDTO sendValidate(@Valid @RequestBody ValidateRequestDTO req, HttpServletRequest request) throws JsonProcessingException {
//        /String channel = channelService.processChannel(request.getHeader("referer"));
//        log.info("referer " + request.getHeader("referer"));
//        log.info("Channel sendValidate " + channel);
        return service.sendValidate(req, "LOAN");
//        return service.sendValidate(req, channel);
    }

}
