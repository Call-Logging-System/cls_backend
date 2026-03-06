package com.cdac.cls_services.call_logs.repositories;

import com.cdac.cls_services.call_logs.dto.CallLogResponseDto;
import com.cdac.cls_services.call_logs.models.CallLogModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

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
        JOIN OfficeModel o ON c.reportedBy = o.id
    """)
    List<CallLogResponseDto> getCallLogs();
}
