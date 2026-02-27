package com.cdac.cls_services.call_logs.repositories;

import com.cdac.cls_services.call_logs.models.CallLogModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CallLogRepository extends JpaRepository<CallLogModel,Integer> {
}
