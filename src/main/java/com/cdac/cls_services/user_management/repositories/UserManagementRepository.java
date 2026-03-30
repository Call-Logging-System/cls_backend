package com.cdac.cls_services.user_management.repositories;

import com.cdac.cls_services.user_management.models.UserModel;
import com.cdac.cls_services.user_management.dto.UserResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserManagementRepository extends JpaRepository<UserModel,Integer> {
    List<UserModel> findByIsActiveTrueAndRole(Long roleId);
    Optional<UserModel> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("""
        SELECT new com.cdac.cls_services.user_management.dto.UserResponseDto(
            u.id,
            u.name,
            u.email,
            u.role
        )
        FROM UserModel u WHERE u.role > 1
    """)
    List<UserResponseDto> getAllUsers();
}
