package com.cdac.cls_services.call_logs.service;

import com.cdac.cls_services.call_logs.dto.AddOfficeReqDto;
import com.cdac.cls_services.call_logs.dto.CallLogResponseDto;
import com.cdac.cls_services.call_logs.models.CallLogModel;

import java.util.List;

public interface CallLogsService {
    List<CallLogResponseDto> getCallLogsList();

    void saveOffice(AddOfficeReqDto dto);
}
