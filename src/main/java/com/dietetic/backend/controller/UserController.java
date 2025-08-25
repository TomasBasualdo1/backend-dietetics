package com.dietetic.backend.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.dietetic.backend.entity.dto.UserRequestDTO;
import com.dietetic.backend.entity.dto.UserResponseDTO;
import com.dietetic.backend.entity.dto.UserWithRoleRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.dietetic.backend.entity.User;
import com.dietetic.backend.service.UserService;
import com.dietetic.backend.entity.Role;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("users")
public class UserController {
    @Autowired
    private UserService userService;

    // GET /users
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.getUserByEmail(auth.getName());
        
        if (currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<User> users = userService.getUsers();
        List<UserResponseDTO> userDTOs = users.stream()
                .map(UserResponseDTO::fromUser)
                .toList();

        return ResponseEntity.ok(userDTOs);
    }

    // GET /users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.getUserByEmail(auth.getName());
        
        if (currentUser.getRole() != Role.ADMIN && !currentUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User user = userService.getUserById(id);
        return ResponseEntity.ok(UserResponseDTO.fromUser(user));
    }

    // GET /users/email/{email}
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.getUserByEmail(auth.getName());
        
        if (currentUser.getRole() != Role.ADMIN && !currentUser.getEmail().equals(email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(UserResponseDTO.fromUser(user));
    }

    // POST /users
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> createUser(UserRequestDTO requestDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.getUserByEmail(auth.getName());
        
        if (currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User savedUser = userService.createUser(
            requestDTO.getEmail(),
            requestDTO.getPassword(),
            requestDTO.getFirstName(),
            requestDTO.getLastName(),
            requestDTO.getAddress(),
            requestDTO.getImage()
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "Usuario de " + savedUser.getFullName() + " creado con éxito"));
    }

    // PUT /users/{id}
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateUser(@PathVariable Long id, UserRequestDTO requestDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.getUserByEmail(auth.getName());
        
        if (currentUser.getRole() != Role.ADMIN && !currentUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User currentUserToUpdate = userService.getUserById(id);

        String email = requestDTO.getEmail() != null ? requestDTO.getEmail() : currentUserToUpdate.getEmail();
        String firstName = requestDTO.getFirstName() != null ? requestDTO.getFirstName() : currentUserToUpdate.getFirstName();
        String lastName = requestDTO.getLastName() != null ? requestDTO.getLastName() : currentUserToUpdate.getLastName();
        String address = requestDTO.getAddress() != null ? requestDTO.getAddress() : currentUserToUpdate.getAddress();
        String password = requestDTO.getPassword() != null ? requestDTO.getPassword() : currentUserToUpdate.getPassword();
        MultipartFile image = requestDTO.getImage() != null ? requestDTO.getImage() : getUserImageMultipartFile(currentUserToUpdate);

        User updatedUser = userService.updateUser(id, email, password, firstName, lastName, address, image);
        return ResponseEntity.ok(UserResponseDTO.fromUser(updatedUser));
    }

    // PUT /users/role/{id}
    @PutMapping("/role/{id}")
    public ResponseEntity<?> updateUserWithRole(@PathVariable Long id, @RequestBody UserWithRoleRequestDTO requestDTO) {
        User currentUserToUpdate = userService.getUserById(id);

        String email = requestDTO.getEmail() != null ? requestDTO.getEmail() : currentUserToUpdate.getEmail();
        String firstName = requestDTO.getFirstName() != null ? requestDTO.getFirstName() : currentUserToUpdate.getFirstName();
        String lastName = requestDTO.getLastName() != null ? requestDTO.getLastName() : currentUserToUpdate.getLastName();
        String address = requestDTO.getAddress() != null ? requestDTO.getAddress() : currentUserToUpdate.getAddress();
        String password = requestDTO.getPassword() != null ? requestDTO.getPassword() : currentUserToUpdate.getPassword();
        MultipartFile image = requestDTO.getImage() != null ? requestDTO.getImage() : getUserImageMultipartFile(currentUserToUpdate);
        Role role = requestDTO.getRole() != null ? requestDTO.getRole() : currentUserToUpdate.getRole();

        User updatedUser = userService.updateUserWithRole(id, email, password, firstName, lastName, address, image, role);
        return ResponseEntity.ok(UserResponseDTO.fromUser(updatedUser));
    }

    // DELETE /users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.getUserByEmail(auth.getName());
        
        if (currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userService.deleteUserById(id);
        return ResponseEntity.ok(Map.of("message", "Usuario eliminado con éxito"));
    }

    private MultipartFile getUserImageMultipartFile(User user){
        if (user.getImageData() == null) {
            return null;
        }
        byte[] bytes;
        try {
            bytes = user.getImageData().getBytes(1, (int) user.getImageData().length());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new MockMultipartFile(
                "image",
                "user-image.jpg",
                user.getImageType(),
                bytes
        );
    }
}
