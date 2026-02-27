package com.cdac.cls_services.call_logs.service;

import com.cdac.cls_services.call_logs.dto.CallLogsListDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CallLogsServiceImpl implements CallLogsService{

    @Override
    public List<CallLogsListDto> getCallLogsList() {
        return List.of();
    }
}
