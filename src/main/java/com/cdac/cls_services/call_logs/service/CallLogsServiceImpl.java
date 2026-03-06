package com.cdac.cls_services.call_logs.service;

import com.cdac.cls_services.call_logs.dto.AddOfficeReqDto;
import com.cdac.cls_services.call_logs.dto.CallLogResponseDto;
import com.cdac.cls_services.call_logs.models.CallLogModel;
import com.cdac.cls_services.call_logs.models.OfficeModel;
import com.cdac.cls_services.call_logs.repositories.CallLogRepository;
import com.cdac.cls_services.call_logs.repositories.OfficeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CallLogsServiceImpl implements CallLogsService{

    private CallLogRepository callLogRepo;
    private OfficeRepository officeRepo;

    @Override
    public List<CallLogResponseDto> getCallLogsList() {
        return callLogRepo.getCallLogs();
    }

    @Override
    public void saveOffice(AddOfficeReqDto dto) {

        boolean exists = officeRepo.existsByOfficeUserName(dto.getUserName());
        if(!exists){
            OfficeModel office = new OfficeModel();
            office.setOfficeUserName(dto.getUserName());
            office.setOfficeLevel(dto.getOfficeLevel());

            officeRepo.save(office);
        }
    }
}
