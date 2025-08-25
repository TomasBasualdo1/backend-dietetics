package com.dietetic.backend.entity.dto;

import org.springframework.web.multipart.MultipartFile;

import com.dietetic.backend.entity.Role;

import lombok.Data;

@Data
public class UserWithRoleRequestDTO {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String address;
    private MultipartFile image;
    private Role role;
}
