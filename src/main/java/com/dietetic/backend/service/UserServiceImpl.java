package com.dietetic.backend.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dietetic.backend.entity.Role;
import com.dietetic.backend.entity.User;
import com.dietetic.backend.exceptions.UserDuplicateException;
import com.dietetic.backend.exceptions.UserNotFoundException;
import com.dietetic.backend.repository.UserRepository;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) throws UserNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public User getUserByEmail(String email) throws UserNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public User createUser(String email, String password, String firstName, String lastName, String address, MultipartFile image) throws UserDuplicateException {
        if (userRepository.existsByEmail(email)) {
            throw new UserDuplicateException(email);
        }
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .firstName(firstName)
                .lastName(lastName)
                .address(address)
                .role(Role.USER)
                .build();

        if (image != null && !image.isEmpty()) {
            try {
                user.setImageData(new SerialBlob(image.getBytes()));
                user.setImageType(image.getContentType());
            } catch (IOException | SQLException e) {
                throw new RuntimeException("Error processing image", e);
            }
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public User updateUser(Long id, String email, String password, String firstName, String lastName, String address, MultipartFile image) throws UserDuplicateException {
        User user = getUserById(id);
        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new UserDuplicateException(email);
        }

        user.setEmail(email);
        if (!user.getPassword().equals(password)) {
            user.setPassword(passwordEncoder.encode(password));
        }
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAddress(address);

        if (image != null && !image.isEmpty()) {
            try {
                user.setImageData(new SerialBlob(image.getBytes()));
                user.setImageType(image.getContentType());
            } catch (IOException | SQLException e) {
                throw new RuntimeException("Error processing image", e);
            }
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public User updateUserWithRole(Long id, String email, String password, String firstName, String lastName, String address, MultipartFile image, Role role) throws UserDuplicateException {
        User user = getUserById(id);
        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new UserDuplicateException(email);
        }

        user.setEmail(email);
        if (!user.getPassword().equals(password)) {
            user.setPassword(passwordEncoder.encode(password));
        }
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAddress(address);
        user.setRole(role);

        if (image != null && !image.isEmpty()) {
            try {
                user.setImageData(new SerialBlob(image.getBytes()));
                user.setImageType(image.getContentType());
            } catch (IOException | SQLException e) {
                throw new RuntimeException("Error processing image", e);
            }
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteUserById(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

}
