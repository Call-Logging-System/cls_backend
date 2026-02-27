package com.cdac.cls_services.call_logs.service;

import com.cdac.cls_services.call_logs.models.CallLogModel;

import java.util.List;

public interface CallLogsService {
    List<CallLogModel> getCallLogsList();
}
