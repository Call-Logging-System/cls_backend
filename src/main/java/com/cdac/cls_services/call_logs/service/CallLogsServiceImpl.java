package com.cdac.cls_services.call_logs.service;

import com.cdac.cls_services.call_logs.dto.*;
import com.cdac.cls_services.call_logs.models.CallLogModel;
import com.cdac.cls_services.call_logs.models.OfficeModel;
import com.cdac.cls_services.call_logs.repositories.CallLogRepository;
import com.cdac.cls_services.call_logs.repositories.OfficeRepository;
import com.cdac.cls_services.call_logs.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@AllArgsConstructor
public class CallLogsServiceImpl implements CallLogsService{

    private CallLogRepository callLogRepo;
    private OfficeRepository officeRepo;
    private UserRepository userRepo;

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

    @Override
    public List<DropdownDto> getUsersDropdown() {
        return userRepo.findByIsActiveTrueAndRole(2L)
                .stream()
                .map(user -> {
                    DropdownDto dto = new DropdownDto();
                    dto.setKey(user.getId().intValue());
                    dto.setValue(user.getName());
                    return dto;
                })
                .toList();
    }

    @Override
    public void saveCallLog(AddCallLogDto dto) {
        OfficeModel office = officeRepo.findByOfficeUserName(dto.getOfficeUserName());

        if(office == null){
            OfficeModel officeModel = new OfficeModel();
            officeModel.setOfficeUserName(dto.getOfficeUserName());
            officeModel.setOfficeLevel(dto.getOfficeLevel());

            office = officeRepo.save(officeModel);
        }

        CallLogModel model = new CallLogModel();
        model.setCallDate(LocalDate.parse(dto.getCallDate()));
        model.setIssueReported(dto.getIssueReported());
        model.setIssueType(dto.getIssueType().charAt(0));
        model.setDescription(dto.getDescription());
        model.setOfficeId(Integer.valueOf(office.getId().toString()));
        model.setReportedTo(dto.getReportedTo());
        model.setSolvedBy(dto.getSolvedBy());
        model.setPriority(dto.getPriority().charAt(0));
        model.setStatus(dto.getStatus().charAt(0));
        model.setIsReleased(dto.getIsReleased().equals("Y") ? true : false);
        model.setCallStartTime(dto.getCallStartTime());
        model.setCallEndTime(dto.getCallEndTime());
        model.setCreatedBy(dto.getReportedTo());

        if (dto.getCallStartTime() != null && dto.getCallEndTime() != null) {
            long totalSeconds = ChronoUnit.SECONDS.between(dto.getCallStartTime(), dto.getCallEndTime());
            long timeTaken = (long) Math.ceil(totalSeconds / 60.0);
            model.setTimeTakenMinutes((int) timeTaken);
        }

        callLogRepo.save(model);
    }

    @Override
    public void delete(DeleteCallLogDto dto) {
        boolean exists = callLogRepo.existsById(dto.getId());

        if(exists){
            callLogRepo.deleteById(dto.getId());
        }
    }

    @Override
    public CallLogResponseDto get(GetCallLogDto dto) {

        return callLogRepo.getCallLogById(dto.getId()).orElseThrow(()-> new RuntimeException("No Call Log Found"));
    }

    @Override
    public void update(AddCallLogDto dto) {
        OfficeModel office = officeRepo.findByOfficeUserName(dto.getOfficeUserName());

        if(office == null){
            OfficeModel officeModel = new OfficeModel();
            officeModel.setOfficeUserName(dto.getOfficeUserName());
            officeModel.setOfficeLevel(dto.getOfficeLevel());

            office = officeRepo.save(officeModel);
        }

        CallLogModel model = callLogRepo.findById(dto.getId()).orElseThrow(()-> new RuntimeException("No Record Found"));

        model.setCallDate(LocalDate.parse(dto.getCallDate()));
        model.setIssueReported(dto.getIssueReported());
        model.setIssueType(dto.getIssueType().charAt(0));
        model.setDescription(dto.getDescription());
        model.setOfficeId(Integer.valueOf(office.getId().toString()));
        model.setReportedTo(dto.getReportedTo());
        model.setSolvedBy(dto.getSolvedBy());
        model.setPriority(dto.getPriority().charAt(0));
        model.setStatus(dto.getStatus().charAt(0));
        model.setIsReleased(dto.getIsReleased().equals("Y") ? true : false);
        model.setCallStartTime(dto.getCallStartTime());
        model.setCallEndTime(dto.getCallEndTime());

        if (dto.getCallStartTime() != null && dto.getCallEndTime() != null) {
            long totalSeconds = ChronoUnit.SECONDS.between(dto.getCallStartTime(), dto.getCallEndTime());
            long timeTaken = (long) Math.ceil(totalSeconds / 60.0);
            model.setTimeTakenMinutes((int) timeTaken);
        }

        callLogRepo.save(model);
    }
}
