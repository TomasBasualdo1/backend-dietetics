package com.dietetic.backend.service;

import java.util.List;

import com.dietetic.backend.entity.Role;
import com.dietetic.backend.entity.User;
import com.dietetic.backend.exceptions.UserDuplicateException;
import com.dietetic.backend.exceptions.UserNotFoundException;
import org.springframework.web.multipart.MultipartFile;


public interface UserService {
    List<User> getUsers();

    User getUserById(Long id) throws UserNotFoundException;

    User getUserByEmail(String email) throws UserNotFoundException;

    User createUser(String email, String password, String firstName, String lastName, String address, MultipartFile image) throws UserDuplicateException;

    User updateUser(Long id, String email, String password, String firstName, String lastName, String address, MultipartFile image) throws UserDuplicateException;

    User updateUserWithRole(Long id, String email, String password, String firstName, String lastName, String address, MultipartFile image, Role role) throws UserDuplicateException;

    void deleteUserById(Long id);

}
