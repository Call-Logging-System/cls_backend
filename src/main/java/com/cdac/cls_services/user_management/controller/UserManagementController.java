package com.cdac.cls_services.user_management.controller;

import com.cdac.cls_services.call_logs.dto.ResponseDto;
import com.cdac.cls_services.call_logs.models.OfficeModel;
import com.cdac.cls_services.user_management.dto.SaveUserDto;
import com.cdac.cls_services.user_management.dto.UpdateUserDto;
import com.cdac.cls_services.user_management.dto.UserResponseDto;
import com.cdac.cls_services.user_management.service.UserManagementService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user_management")
@AllArgsConstructor
public class UserManagementController {
    private final UserManagementService userManagementService;

    @GetMapping("/users")
    public List<UserResponseDto> getUsers() {
        return userManagementService.getUsers();
    }

    @PostMapping("/save")
    public ResponseEntity<ResponseDto> saveUser(@RequestBody SaveUserDto dto){
        userManagementService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto("201","Office created successfully"));
    }

    @PostMapping("/delete")
    public ResponseEntity<ResponseDto> deleteUser(@RequestBody Integer id){
        userManagementService.delete(id);
        return ResponseEntity.ok(new ResponseDto("200","Office deleted successfully"));
    }

    @PostMapping("/update")
    public ResponseEntity<ResponseDto> updateUser(@RequestBody UpdateUserDto dto){
        userManagementService.update(dto);
        return ResponseEntity.ok(new ResponseDto("200","Office updated successfully"));
    }

}
