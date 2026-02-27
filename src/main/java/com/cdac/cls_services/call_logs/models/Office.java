package com.cdac.cls_services.call_logs.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "office_m")
public class Office {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "office_user_name")
    private String officeUserName;

    @Column(name = "office_level")
    private Long officeLevel;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "alternate_contact_number")
    private String alternateContactNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "address")
    private String address;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

}
