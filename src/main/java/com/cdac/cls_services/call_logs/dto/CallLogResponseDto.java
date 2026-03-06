package com.cdac.cls_services.call_logs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CallLogResponseDto {
    private Long id;
    private LocalDate callDate;
    private String issueReported;
    private Character issueType;
    private String reportedBy;
    private Character status;
    private Integer timeTakenMinutes;
}
