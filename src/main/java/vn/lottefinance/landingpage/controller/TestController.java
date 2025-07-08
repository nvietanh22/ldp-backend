package vn.lottefinance.landingpage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.lottefinance.landingpage.services.CacheService;

@RestController
@RequestMapping("/public")
public class TestController {
    @Autowired
    private CacheService cacheService;

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
}
