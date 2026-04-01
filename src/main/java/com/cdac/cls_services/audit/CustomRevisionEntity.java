package com.cdac.cls_services.audit;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

@Entity
@RevisionEntity(CustomRevisionListener.class)
@Table(name = "aud_revision_info")
@Data
public class CustomRevisionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RevisionNumber
    @Column(name = "rev")
    private long rev;

    @RevisionTimestamp
    @Column(name = "rev_tstmp", nullable = false)
    private long revTimestamp;          // epoch-millis; Envers manages this

    @Column(name = "user_id")
    private Integer userId;           // populated by ClsRevisionListener

    @Column(name = "ip_address", length = 45)   // 45 chars covers IPv6
    private String ipAddress;
}
