package com.cdac.cls_services.phone_book.controller;

import com.cdac.cls_services.call_logs.models.OfficeModel;
import com.cdac.cls_services.phone_book.dto.OfficeDto;
import com.cdac.cls_services.phone_book.service.PhoneBookService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phone_book")
@AllArgsConstructor
public class PhoneBookController {
    private final PhoneBookService phoneBookService;

    @GetMapping("/offices")
    public List<OfficeModel> getOffices() {
        return phoneBookService.getOffices();
    }

    @PostMapping("/getOfficeByUserName")
    public OfficeDto getOfficeByUserName(@RequestBody String userName) {
        return phoneBookService.getOfficeByUserName(userName);
    }
}
