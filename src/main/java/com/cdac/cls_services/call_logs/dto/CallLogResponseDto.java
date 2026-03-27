package com.cdac.cls_services.call_logs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallLogResponseDto {
    private Long id;
    private LocalDate callDate;
    private String issueReported;
    private Character issueType;
    private String reportedBy;
    private Character status;
    private Integer timeTakenMinutes;
    private LocalTime callStartTime;
    private LocalTime callEndTime;
    private Character priority;
    private String description;
    private Integer reportedTo;
    private Integer solvedBy;
    private LocalDate releaseDate;
    private Boolean isReleased;
    private String officeUserName;   // from OfficeModel join
    private Integer officeLevel;
    private String reportedToName;
    private String solvedByName;

    public CallLogResponseDto(Long id, LocalDate callDate, String issueReported, Character issueType, String reportedBy, Character status,LocalTime callStartTime, LocalTime callEndTime, Integer timeTakenMinutes, String reportedToName, String solvedByName) {
        this.id = id;
        this.callDate = callDate;
        this.issueReported = issueReported;
        this.issueType = issueType;
        this.reportedBy = reportedBy;
        this.status = status;
        this.callStartTime = callStartTime;
        this.callEndTime = callEndTime;
        this.timeTakenMinutes = timeTakenMinutes;
        this.reportedToName = reportedToName;
        this.solvedByName = solvedByName;
    }

    public CallLogResponseDto(Long id, LocalDate callDate, String issueReported, Character issueType,
                              String reportedBy, Character status, Integer timeTakenMinutes,
                              LocalTime callStartTime, LocalTime callEndTime, Character priority,
                              String description, Integer reportedTo, Integer solvedBy,
                              LocalDate releaseDate, Boolean isReleased, String officeUserName,
                              Integer officeLevel) {
        this.id = id;
        this.callDate = callDate;
        this.issueReported = issueReported;
        this.issueType = issueType;
        this.reportedBy = reportedBy;
        this.status = status;
        this.timeTakenMinutes = timeTakenMinutes;
        this.callStartTime = callStartTime;
        this.callEndTime = callEndTime;
        this.priority = priority;
        this.description = description;
        this.reportedTo = reportedTo;
        this.solvedBy = solvedBy;
        this.releaseDate = releaseDate;
        this.isReleased = isReleased;
        this.officeUserName = officeUserName;
        this.officeLevel = officeLevel;
    }




}
