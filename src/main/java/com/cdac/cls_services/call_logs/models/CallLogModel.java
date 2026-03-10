package com.cdac.cls_services.call_logs.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "call_log_m")
public class CallLogModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "call_date", nullable = false)
    private LocalDate callDate;

    @Column(name = "issue_reported" , nullable = false)
    private String issueReported;

    @Column(name = "issue_type", nullable = false)
    private Character issueType;

    @Column(name = "description")
    private String description;

    @Column(name = "reported_by", nullable = false)
    private Integer reportedBy;

    @Column(name = "reported_to", nullable = false)
    private Integer reportedTo;

    @Column(name = "solved_by")
    private Integer solvedBy;

    @Column(name = "priority")
    private Character priority;

    @Column(name = "status")
    private Character status;

    @Column(name = "is_released")
    private Boolean isReleased;

    @Column(name = "call_start_time", nullable = false)
    private LocalTime callStartTime;

    @Column(name = "call_end_time")
    private LocalTime callEndTime;

    @Column(name = "time_taken_minutes")
    private Integer timeTakenMinutes;

    @Column(name = "created_by", nullable = false)
    private Integer createdBy;

    @CreationTimestamp
    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

}
