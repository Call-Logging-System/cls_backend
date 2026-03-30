package com.cdac.cls_services.user_management.dto;

import lombok.Data;

@Data
public class UpdateUserDto {
    private Integer userId;
    private String userName;
    private String email;
    private Integer role;
}
