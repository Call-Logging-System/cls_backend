package com.cdac.cls_services.user_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseDto {
    private Long userId;
    private String userName;
    private String email;
    private Integer role;
}
