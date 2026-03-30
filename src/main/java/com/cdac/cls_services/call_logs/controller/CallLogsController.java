package com.cdac.cls_services.call_logs.controller;

import com.cdac.cls_services.call_logs.dto.*;
import com.cdac.cls_services.call_logs.service.CallLogsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<ResponseDto> deleteCallLog(@RequestBody Integer id){
        callLogsService.delete(id);
        return ResponseEntity.ok(new ResponseDto("200", "Call Log deleted successfully"));
    }

    @PostMapping("/get")
    public CallLogResponseDto getCallLog(@RequestBody Integer id) {
        return callLogsService.get(id);
    }

    @PostMapping("/update")
    public ResponseEntity<ResponseDto> updateCallLog(@RequestBody AddCallLogDto dto){
        callLogsService.update(dto);
        return ResponseEntity.ok(new ResponseDto("200","Call updated successfully"));
    }

    @PostMapping("/export/all")
    public ResponseEntity<byte[]> exportAllCallLogs() {
        byte[] excelBytes = callLogsService.exportAllCallLogs();

        String filename = "Forest_CallRegister_Updated";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(excelBytes.length)
                .body(excelBytes);
    }
}
