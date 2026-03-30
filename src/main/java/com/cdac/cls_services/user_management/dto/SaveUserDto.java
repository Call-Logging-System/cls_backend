package com.cdac.cls_services.user_management.dto;

import lombok.Data;

@Data
public class SaveUserDto {
    private String userName;
    private String email;
    private String password;
    private Integer role;
}
