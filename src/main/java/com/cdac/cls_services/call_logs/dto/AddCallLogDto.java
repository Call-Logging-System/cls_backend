package com.cdac.cls_services.call_logs.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AddCallLogDto {
    private Integer id;
    private String officeUserName;
    private Integer officeLevel;
    private String issueReported;
    private String callDate;
    private String issueType;
    private String priority;
    private String description;
    private Integer reportedTo;
    private String reportedBy;
    private String status;
    private LocalTime callStartTime;
    private LocalTime callEndTime;
    private Integer solvedBy;
    private Boolean isReleased;
    private LocalDate releaseDate;
    private Integer timeTakenMinutes;
}
