package com.cdac.cls_services.call_logs.repositories;

import com.cdac.cls_services.call_logs.dto.CallLogResponseDto;
import com.cdac.cls_services.call_logs.models.CallLogModel;
import com.cdac.cls_services.call_logs.models.OfficeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfficeRepository extends JpaRepository<OfficeModel,Integer> {
    boolean existsByOfficeUserName(String officeUserName);
    OfficeModel findByOfficeUserName(String officeUserName);
    @Query("SELECT o FROM OfficeModel o WHERE LOWER(o.officeUserName) LIKE LOWER(CONCAT('%', :userName, '%'))")
    List<OfficeModel> findByOfficeUserNameContainingIgnoreCase(@Param("userName") String userName);
}
