package com.cdac.cls_services.audit;
import com.cdac.cls_services.user_management.repositories.UserManagementRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
public class CustomRevisionListener implements RevisionListener{

    private static UserManagementRepository userRepo;


    public static void setUserRepo(UserManagementRepository repo) {
        CustomRevisionListener.userRepo = repo;
    }

    @Override
    public void newRevision(Object revisionEntity) {
        try {
            CustomRevisionEntity rev = (CustomRevisionEntity) revisionEntity;
            rev.setUserId(resolveUserId());
            rev.setIpAddress(resolveIpAddress());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Integer resolveUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()
                    || "anonymousUser".equals(auth.getPrincipal())) return null;

            String email = auth.getName(); // principal name = email in your setup
            return userRepo.findByEmail(email)
                    .map(u -> Math.toIntExact(u.getId()))
                    .orElse(null);
        } catch (Exception e) {
            log.debug("Could not resolve userId for audit revision: {}", e.getMessage());
            return null;
        }
    }

    private String resolveIpAddress() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return null;

            HttpServletRequest request = attrs.getRequest();

            // Honour reverse-proxy / load-balancer forwarding headers
            String ip = request.getHeader("X-Forwarded-For");
            if (ip != null && !ip.isBlank()) {
                // X-Forwarded-For can be a comma-separated chain; first entry is client
                return ip.split(",")[0].strip();
            }
            ip = request.getHeader("X-Real-IP");
            if (ip != null && !ip.isBlank()) return ip.strip();

            return request.getRemoteAddr();
        } catch (Exception e) {
            log.debug("Could not resolve IP for audit revision: {}", e.getMessage());
            return null;
        }
    }
}
