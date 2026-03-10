package com.cdac.cls_services.call_logs.controller;

import com.cdac.cls_services.call_logs.dto.*;
import com.cdac.cls_services.call_logs.models.CallLogModel;
import com.cdac.cls_services.call_logs.service.CallLogsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/call_logs")
@AllArgsConstructor
public class CallLogsController {
    private final CallLogsService callLogsService;

    @GetMapping("/getList")
    public List<CallLogResponseDto> getCallLogsList() {
        return callLogsService.getCallLogsList();
    }

    @PostMapping("/office/save")
    public ResponseEntity<ResponseDto> saveOffice(@RequestBody AddOfficeReqDto dto){
        callLogsService.saveOffice(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto("201","Office created successfully"));
    }

    @GetMapping("/users/dropdown")
    public List<DropdownDto> getUsersDropdown() {
        return callLogsService.getUsersDropdown();
    }

    @PostMapping("/save")
    public ResponseEntity<ResponseDto> saveCallLog(@RequestBody AddCallLogDto dto){
        callLogsService.saveCallLog(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto("201","Call saved successfully"));
    }

    @PostMapping("/delete")
    public ResponseEntity<ResponseDto> deleteCallLog(@RequestBody DeleteCallLogDto dto){
        callLogsService.delete(dto);
        return ResponseEntity.ok(new ResponseDto("200", "Call Log deleted successfully"));
    }
}
