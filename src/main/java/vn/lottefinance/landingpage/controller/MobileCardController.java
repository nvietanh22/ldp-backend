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
import vn.lottefinance.landingpage.client.custom.EsbWareHouseClient;
import vn.lottefinance.landingpage.dto.DataDto;
import vn.lottefinance.landingpage.dto.ResponseDto;
import vn.lottefinance.landingpage.dto.card.*;
import vn.lottefinance.landingpage.services.ChannelService;
import vn.lottefinance.landingpage.services.MobileCardService;

@RestController
@RequestMapping("/api/mobile-cards")
@RequiredArgsConstructor
@Slf4j
public class MobileCardController {

    private final MobileCardService service;

    private final ChannelService channelService;

    private final EsbWareHouseClient esbWareHouseClient;

    private final MobileCardService mobileCardService;

    @PostMapping("/spin-result")
    @ResponseStatus(HttpStatus.OK)
    public SpinResultResponseDTO getSpinResult(@Valid @RequestBody SpinResultRequestDTO request) {
        return service.getSpinResult(request);
    }

    @PostMapping("/get-card")
    @ResponseStatus(HttpStatus.OK)
    public GetMobileCardResponseDTO getCardBy(@Valid @RequestBody GetMobileCardRequestDTO request) {
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

    @PostMapping("/verify-phone")
    public ValidateResponseDTO sendValidate(@Valid @RequestBody ValidateRequestDTO req, HttpServletRequest request) throws JsonProcessingException {
//        String channel = channelService.processChannel(request.getHeader("referer"));
//        log.info("referer " + request.getHeader("referer"));
//        log.info("Channel sendValidate " + channel);
//        log.info("Start verify-phone: {}", req);

        return service.sendValidate(req, "LOAN");
//        return service.sendValidate(req, channel);
    }

    @PostMapping("/get-price")
    @ResponseStatus(HttpStatus.OK)
    public GetCardResponseDTO getActivePriceByBrandService(@Valid @RequestBody GetListCardActiveByBrandRequestDTO request) {
        return esbWareHouseClient.getActivePriceByBrandService(request);
    }

    @PostMapping("/minigame-process")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ResponseDto> process(@RequestBody DataDto dto, HttpServletRequest request) {
//        String channel =  channelService.processChannel(request.getHeader("referer"));
//        return ResponseEntity.ok(esbWareHouseClient.saveWarehouse(dto, channel));
        return ResponseEntity.ok(ResponseDto.builder()
                .reason_code("0")
                .rslt_msg("Success")
                .build());
    }

    @PostMapping("/generate-layout")
    @ResponseStatus(HttpStatus.OK)
    public WheelLayoutResponseDTO generateLayout(@RequestBody GenerateLayoutRequestDTO request) {
        return service.generateAndCacheLayout(request.getBrand());
    }

}
