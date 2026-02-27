package com.cdac.cls_services.call_logs.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "call_log_m")
public class CallLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "call_date")
    private LocalDate callDate;

    @Column(name = "issue_reported")
    private String issueReported;

    @Column(name = "issue_type")
    private Character issueType;

    @Column(name = "description")
    private String description;

    @Column(name = "reported_by")
    private Integer reportedBy;

    @Column(name = "reported_to")
    private Integer reportedTo;

    @Column(name = "solved_by")
    private Integer solvedBy;

    @Column(name = "priority")
    private Character priority;

    @Column(name = "status")
    private Character status;

    @Column(name = "is_released")
    private Boolean isReleased;

    @Column(name = "call_start_time")
    private LocalDateTime callStartTime;

    @Column(name = "call_end_time")
    private LocalDateTime callEndTime;

    @Column(name = "time_taken_minutes")
    private Integer timeTakenMinutes;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

}
