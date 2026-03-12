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
            c.timeTakenMinutes
        )
        FROM CallLogModel c
        JOIN OfficeModel o ON c.officeId = o.id
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
        o.officeLevel
    )
    FROM CallLogModel c
    JOIN OfficeModel o ON c.officeId = o.id
    WHERE c.id = :id
""")
    Optional<CallLogResponseDto> getCallLogById(@Param("id") Integer id);
}
