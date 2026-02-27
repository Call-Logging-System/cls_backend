package com.cdac.cls_services.call_logs.service;

import com.cdac.cls_services.call_logs.dto.CallLogsListDto;

import java.util.List;

public interface CallLogsService {
    List<CallLogsListDto> getCallLogsList();
}
