package com.cdac.cls_services.call_logs.controller;

import com.cdac.cls_services.call_logs.dto.AddOfficeReqDto;
import com.cdac.cls_services.call_logs.dto.CallLogResponseDto;
import com.cdac.cls_services.call_logs.dto.CallLogsListDto;
import com.cdac.cls_services.call_logs.dto.ResponseDto;
import com.cdac.cls_services.call_logs.models.CallLogModel;
import com.cdac.cls_services.call_logs.service.CallLogsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto("200","Office created successfully")); // To-do - Change the status code to created status code
    }
}
