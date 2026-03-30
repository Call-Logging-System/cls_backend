package com.cdac.cls_services.call_logs.repositories;

import com.cdac.cls_services.call_logs.dto.CallLogResponseDto;
import com.cdac.cls_services.call_logs.models.CallLogModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CallLogRepository extends JpaRepository<CallLogModel,Integer> {

    @Query("""
        SELECT new com.cdac.cls_services.call_logs.dto.CallLogResponseDto(
            c.id,
            c.callDate,
            c.issueReported,
            c.issueType,
            o.officeUserName,
            c.status,
            c.callStartTime,
            c.callEndTime,
            c.timeTakenMinutes,
            reportedToUser.name,
            solvedByUser.name
        )
        FROM CallLogModel c
        JOIN OfficeModel o ON c.officeId = o.id
        LEFT JOIN UserModel reportedToUser ON c.reportedTo = reportedToUser.id
        LEFT JOIN UserModel solvedByUser ON c.solvedBy = solvedByUser.id
        ORDER BY c.callDate ASC, c.callStartTime ASC
    """)
    List<CallLogResponseDto> getCallLogs();

    @Query("""
    SELECT new com.cdac.cls_services.call_logs.dto.CallLogResponseDto(
        c.id,
        c.callDate,
        c.issueReported,
        c.issueType,
        c.reportedBy,
        c.status,
        c.timeTakenMinutes,
        c.callStartTime,
        c.callEndTime,
        c.priority,
        c.description,
        c.reportedTo,
        c.solvedBy,
        null,
        c.isReleased,
        o.officeUserName,
        o.officeLevel,
        o.contactNumber
    )
    FROM CallLogModel c
    JOIN OfficeModel o ON c.officeId = o.id
    WHERE c.id = :id
""")
    Optional<CallLogResponseDto> getCallLogById(@Param("id") Integer id);
}
