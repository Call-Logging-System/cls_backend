package com.cdac.cls_services.user_management.service;

import com.cdac.cls_services.user_management.dto.SaveUserDto;
import com.cdac.cls_services.user_management.dto.UpdateUserDto;
import com.cdac.cls_services.user_management.dto.UserResponseDto;

import java.util.List;

public interface UserManagementService {
    List<UserResponseDto> getUsers();

    void save(SaveUserDto dto);

    void delete(Integer id);

    void update(UpdateUserDto dto);
}
