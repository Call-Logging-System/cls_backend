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

    public CallLogResponseDto(Long id, LocalDate callDate, String issueReported, Character issueType, String reportedBy, Character status, Integer timeTakenMinutes) {
        this.id = id;
        this.callDate = callDate;
        this.issueReported = issueReported;
        this.issueType = issueType;
        this.reportedBy = reportedBy;
        this.status = status;
        this.timeTakenMinutes = timeTakenMinutes;
    }


}
