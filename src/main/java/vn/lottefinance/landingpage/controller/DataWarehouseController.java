package vn.lottefinance.landingpage.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.lottefinance.landingpage.client.custom.EsbWareHouseClient;
import vn.lottefinance.landingpage.dto.DataDto;
import vn.lottefinance.landingpage.dto.ResponseDto;
import vn.lottefinance.landingpage.services.ChannelService;

@RestController
@RequestMapping("/api/warehouse")
@Validated
public class DataWarehouseController {
    @Autowired
    private EsbWareHouseClient client;

    @Autowired
    private ChannelService channelService;

    @PostMapping("/process")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ResponseDto> process(@RequestBody DataDto dto, HttpServletRequest request) {
        String channel =  channelService.processChannel(request.getHeader("referer"));
        return ResponseEntity.ok(client.sentWareHouse(dto, channel));
    }
}
