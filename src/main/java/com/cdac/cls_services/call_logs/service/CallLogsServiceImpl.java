package com.cdac.cls_services.call_logs.service;

import com.cdac.cls_services.call_logs.models.CallLogModel;
import com.cdac.cls_services.call_logs.repositories.CallLogRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CallLogsServiceImpl implements CallLogsService{

    private CallLogRepository callLogRepo;

    @Override
    public List<CallLogModel> getCallLogsList() {
        return callLogRepo.findAll();
    }
}
