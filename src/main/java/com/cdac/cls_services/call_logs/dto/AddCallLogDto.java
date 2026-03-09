package com.cdac.cls_services.call_logs.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class AddCallLogDto {
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
    private String isReleased;
    private String releaseDate;
}
