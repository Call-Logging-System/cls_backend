package com.cdac.cls_services.call_logs.controller;

import com.cdac.cls_services.call_logs.dto.CallLogsListDto;
import com.cdac.cls_services.call_logs.models.CallLogModel;
import com.cdac.cls_services.call_logs.service.CallLogsService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/call_logs")
@AllArgsConstructor
public class CallLogsController {
    private final CallLogsService callLogsService;

    @GetMapping("/getList")
    public List<CallLogModel> getCallLogsList() {
        return callLogsService.getCallLogsList();
    }
}
