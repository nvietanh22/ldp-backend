package vn.lottefinance.landingpage.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.lottefinance.landingpage.client.custom.EsbWareHouseClient;
import vn.lottefinance.landingpage.dto.card.*;
import vn.lottefinance.landingpage.services.CacheService;
import vn.lottefinance.landingpage.services.MobileCardService;

@RestController
@RequestMapping("/public")
public class TestController {
    @Autowired
    private CacheService cacheService;

    @Autowired
    private EsbWareHouseClient esbWareHouseClient;

    @Autowired
    private MobileCardService mobileCardService;
    @GetMapping("/cache/put")
    public ResponseEntity<String> put(@RequestParam String key, @RequestParam String value) {
        cacheService.putInCache(key, value);
        return ResponseEntity.ok("success");
    }

    @GetMapping("/cache/get")
    public ResponseEntity<String> get(@RequestParam String key) {

        return ResponseEntity.ok(cacheService.getFromCache(key).orElse("not found"));
    }

    @GetMapping("/cache/delete")
    public ResponseEntity<String> delete(@RequestParam String key) {
        cacheService.deleteCache(key);
        return ResponseEntity.ok("success");
    }

//    @GetMapping("/test")
//    public ResponseEntity<String> test() {
//        return ResponseEntity.ok(esbClient.sentWareHouse());
//    }

    @PostMapping("/check-phone")
    public ResponseEntity<String> checkPhoneExits(@RequestBody CheckPhoneExitsRequestDTO phoneNumber) {
        String dataJson = esbWareHouseClient.checkPhoneExitsOnlyData(phoneNumber);
        return ResponseEntity.ok(dataJson);
    }

    @PostMapping("/find-phone")
    public ResponseEntity<String> findPhoneToken(@RequestBody FindPhoneAndTokenRequestDTO requestDTO) {
        String dataJson = esbWareHouseClient.findPhoneToken(requestDTO);
        return ResponseEntity.ok(dataJson);
    }

    @PostMapping("/get-card")
    @ResponseStatus(HttpStatus.OK)
    public GetMobileCardResponseDTO getCardBy(@Valid @RequestBody GetMobileCardRequestDTO request) {
        return mobileCardService.getCardNumber(request);
    }

    @PostMapping("/get-price")
    @ResponseStatus(HttpStatus.OK)
    public String getActivePriceByBrandService(@Valid @RequestBody GetListCardActiveByBrandRequestDTO request) {
        return esbWareHouseClient.getActivePriceByBrandService(request);
    }


}
