package com.cdac.cls_services.call_logs.service;

import com.cdac.cls_services.call_logs.dto.*;
import com.cdac.cls_services.call_logs.models.CallLogModel;
import com.cdac.cls_services.call_logs.models.OfficeModel;
import com.cdac.cls_services.call_logs.repositories.CallLogRepository;
import com.cdac.cls_services.call_logs.repositories.OfficeRepository;
import com.cdac.cls_services.user_management.repositories.UserManagementRepository;
import com.cdac.cls_services.generic.ExcelColumn;
import com.cdac.cls_services.generic.ExcelEngine;
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
    private UserManagementRepository userRepo;
    private ExcelEngine excelService;

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
    public void delete(Integer id) {
        boolean exists = callLogRepo.existsById(id);

        if(exists){
            callLogRepo.deleteById(id);
        }
    }

    @Override
    public CallLogResponseDto get(Integer id) {

        return callLogRepo.getCallLogById(id).orElseThrow(()-> new RuntimeException("No Call Log Found"));
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

        if (dto.getTimeTakenMinutes() != null) {
            model.setTimeTakenMinutes(dto.getTimeTakenMinutes());
        } else if (dto.getCallStartTime() != null && dto.getCallEndTime() != null) {
            // fallback: recalculate only if not provided
            long totalSeconds = ChronoUnit.SECONDS.between(dto.getCallStartTime(), dto.getCallEndTime());
            long timeTaken = (long) Math.ceil(totalSeconds / 60.0);
            model.setTimeTakenMinutes((int) timeTaken);
        }

        callLogRepo.save(model);
    }

    @Override
    public List<OfficeModel> getOffices() {
        return officeRepo.findAll();
    }

    @Override
    public byte[] exportAllCallLogs() {

        List<CallLogResponseDto> logs = callLogRepo.getCallLogs();

        List<ExcelColumn<CallLogResponseDto>> columns = List.of(

                ExcelColumn.<CallLogResponseDto>builder()
                        .header("Date")
                        .width(13)
                        .type(ExcelColumn.CellType.DATE)
                        .value(CallLogResponseDto::getCallDate)
                        .build(),

                ExcelColumn.<CallLogResponseDto>builder()
                        .header("Issue Reported")
                        .width(13)
                        .value(log -> "Forest Maharashtra : Version 2 : " +
                                (log.getReportedBy() != null ? log.getReportedBy() : ""))
                        .build(),

                ExcelColumn.<CallLogResponseDto>builder()
                        .header("Type(Bug,Support,Change,Backend)")
                        .width(13)
                        .value(log -> resolveIssueType(String.valueOf(log.getIssueType())))
                        .build(),

                ExcelColumn.<CallLogResponseDto>builder()
                        .header("Description")
                        .width(13)
                        .value(CallLogResponseDto::getIssueReported)
                        .build(),

                ExcelColumn.<CallLogResponseDto>builder()
                        .header("Issue Reported To")
                        .width(13)
                        .value(log -> log.getReportedToName() != null ? getFirstName(log.getReportedToName()) : "")
                        .build(),

                ExcelColumn.<CallLogResponseDto>builder()
                        .header("Repoted BY (Division/name of Person)")
                        .width(13)
                        .value(CallLogResponseDto::getReportedBy)
                        .build(),

                ExcelColumn.<CallLogResponseDto>builder()
                        .header("Is Released")
                        .width(13)
                        .value(log -> Boolean.TRUE.equals(log.getIsReleased()) ? "Yes" : "No")
                        .build(),

                ExcelColumn.<CallLogResponseDto>builder()
                        .header("Release Date ")
                        .width(13)
                        .type(ExcelColumn.CellType.DATE)
                        .value(CallLogResponseDto::getReleaseDate)
                        .build(),

                ExcelColumn.<CallLogResponseDto>builder()
                        .header("Call Start Time")
                        .width(13)
                        .type(ExcelColumn.CellType.TIME)
                        .value(CallLogResponseDto::getCallStartTime)
                        .build(),

                ExcelColumn.<CallLogResponseDto>builder()
                        .header("Call END Time")
                        .width(13)
                        .type(ExcelColumn.CellType.TIME)
                        .value(CallLogResponseDto::getCallEndTime)
                        .build(),

                ExcelColumn.<CallLogResponseDto>builder()
                        .header("Issue Actually Solved By")
                        .width(13)
                        .value(log -> log.getSolvedByName() != null ? getFirstName(log.getSolvedByName()) : "")
                        .build(),

                ExcelColumn.<CallLogResponseDto>builder()
                        .header("Time Taken for Closing Issue(in Hrs)")
                        .width(13)
                        .value(log -> {
                            if (log.getTimeTakenMinutes() == null || log.getTimeTakenMinutes() <= 0) return "";
                            int hrs  = log.getTimeTakenMinutes() / 60;
                            int mins = log.getTimeTakenMinutes() % 60;
                            return hrs > 0
                                    ? (mins > 0 ? hrs + "hr " + mins + "min" : hrs + "hr")
                                    : mins + "min";
                        })
                        .build()
        );

        // Groups rows by month automatically → one sheet per month (e.g. "Feb26", "Mar25")
        return excelService.exportByMonth(logs, columns, CallLogResponseDto::getCallDate);
    }

    private String resolveIssueType(String c) {
        if (c == null) return "";
        return switch (c) {
            case "B" -> "Bug";
            case "S" -> "Support";
            case "C" -> "Change";
            case "K" -> "Backend";
            default  -> c;
        };
    }

    private String getFirstName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "";
        return fullName.trim().split("\\s+")[0];
    }

}
