package com.dietetic.backend.entity.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class UserRequestDTO {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String address;
    private MultipartFile image;
}
