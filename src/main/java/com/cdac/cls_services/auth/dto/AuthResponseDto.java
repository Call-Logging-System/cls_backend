package com.cdac.cls_services.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDto {
    private Long userId;
    private String name;
    private String email;
    private Integer role;
    private String message;
}
