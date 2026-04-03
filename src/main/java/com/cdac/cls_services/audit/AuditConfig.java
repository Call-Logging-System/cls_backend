package com.cdac.cls_services.audit;

import com.cdac.cls_services.user_management.repositories.UserManagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditConfig {

    private final UserManagementRepository userRepo;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        CustomRevisionListener.setUserRepo(userRepo);
    }
}
