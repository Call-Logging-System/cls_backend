package com.cdac.cls_services.user_management.service;

import com.cdac.cls_services.exception.RecordAlreadyExistsException;
import com.cdac.cls_services.exception.RecordNotFoundException;
import com.cdac.cls_services.user_management.dto.SaveUserDto;
import com.cdac.cls_services.user_management.dto.UpdateUserDto;
import com.cdac.cls_services.user_management.dto.UserResponseDto;
import com.cdac.cls_services.user_management.models.UserModel;
import com.cdac.cls_services.user_management.repositories.UserManagementRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {
    private final UserManagementRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponseDto> getUsers() {
        return userRepo.getAllUsers();
    }

    @Override
    public void save(SaveUserDto dto) {
        boolean exists = userRepo.existsByEmail(dto.getEmail());

        if(exists){
            throw new RecordAlreadyExistsException("User already exists");
        }else{
            UserModel user = new UserModel();
            user.setName(dto.getUserName());
            user.setEmail(dto.getEmail());
            user.setIsActive(true);
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user.setRole(dto.getRole());
            userRepo.save(user);
        }
    }

    @Override
    public void delete(Integer id) {
        boolean exists = userRepo.existsById(id);

        if(exists){
            userRepo.deleteById(id);
        }else{
            throw new RecordNotFoundException("User does not exist");
        }
    }

    @Override
    public void update(UpdateUserDto dto) {
        UserModel user = userRepo.findById(dto.getUserId()).orElseThrow(() -> new RecordNotFoundException("User does not exist"));

        user.setName(dto.getUserName());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());

        userRepo.save(user);
    }
}
