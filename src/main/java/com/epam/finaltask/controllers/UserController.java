package com.epam.finaltask.controllers;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.exception.EntityNotFoundException;
import com.epam.finaltask.exception.StatusCodes;
import com.epam.finaltask.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult) {
        logger.info("Received request to register a new user with id: {}", userDTO.getId());
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errorMessage.append(fieldError.getDefaultMessage());
            }
            logger.warn("Validation errors: {}", errorMessage);
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.INVALID_DATA.name());
            response.put("statusMessage", errorMessage.toString().trim());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            UserDTO registeredUser = userService.register(userDTO);
            logger.info("User successfully registered: {}", registeredUser.getId());
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.OK.name());
            response.put("statusMessage", "User is successfully registered");
            response.put("data", registeredUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            logger.error("Error during user registration: {}", ex.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.INVALID_DATA.name());
            response.put("statusMessage", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        logger.info("Received request to get user by username: {}", username);
        try {
            UserDTO userDTO = userService.getUserByUsername(username);
            logger.info("User successfully received with id: {}", userDTO.getId());
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.OK.name());
            response.put("statusMessage", "User was obtained successfully");
            response.put("data", userDTO);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException ex) {
            logger.error("User not found: {}", ex.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.NOT_FOUND.name());
            response.put("statusMessage", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalStateException ex) {
            logger.error("Error during getting user: {}", ex.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.BAD_REQUEST.name());
            response.put("statusMessage", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PatchMapping("/{username}/balance")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<?> updateBalance(@PathVariable String username, @RequestParam double amount) {
        logger.info("Received request to update balance for user: {}, amount: {}", username, amount);
        try {
            UserDTO updatedUser = userService.updateBalance(username, amount);
            logger.info("Balance successfully updated for user: {}, new balance: {}", username, updatedUser.getBalance());
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.OK.name());
            response.put("statusMessage", "Balance updated successfully");
            response.put("data", updatedUser);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException ex) {
            logger.error("User not found for balance update: {}", ex.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.NOT_FOUND.name());
            response.put("statusMessage", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalArgumentException ex) {
            logger.error("Error during balance update: {}", ex.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.INVALID_DATA.name());
            response.put("statusMessage", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/allUsers")
    @PreAuthorize("hasAuthority('ADMIN_READ')")
    public ResponseEntity<?> getAllUsers() {
        logger.info("Received request to get all users");
        try {
            List<UserDTO> users = userService.getAllUsers();
            logger.info("All users successfully received");
            logger.info("Found {} users.", users.size());
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.OK.name());
            response.put("statusMessage", "Users obtained successfully");
            response.put("data", users);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Error during getting all users: {}", ex.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.INTERNAL_ERROR.name());
            response.put("statusMessage", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PatchMapping("/changeAccountStatus")
    @PreAuthorize("hasAuthority('ADMIN_UPDATE')")
    public ResponseEntity<?> changeAccountStatus(@RequestBody UserDTO userDTO) {
        logger.info("Received request to change account status for user: {}", userDTO.getId());
        try {
            UserDTO updatedUser = userService.changeAccountStatus(userDTO);
            logger.info("User account status successfully updated: {}, new status: {}", updatedUser.getId(), updatedUser.isAccountStatus());
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.OK.name());
            response.put("statusMessage", "User status updated successfully");
            response.put("data", updatedUser);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException ex) {
            logger.error("User not found for status update: {}", ex.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.NOT_FOUND.name());
            response.put("statusMessage", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception ex) {
            logger.error("Error during status update: {}", ex.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.INTERNAL_ERROR.name());
            response.put("statusMessage", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
