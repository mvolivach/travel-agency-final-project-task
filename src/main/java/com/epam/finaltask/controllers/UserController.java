package com.epam.finaltask.controllers;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.exception.EntityNotFoundException;
import com.epam.finaltask.exception.StatusCodes;
import com.epam.finaltask.service.UserService;
import jakarta.validation.Valid;
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

    @Autowired
    private UserService userService;


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errorMessage.append(fieldError.getDefaultMessage());
            }
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.INVALID_DATA.name());
            response.put("statusMessage", errorMessage.toString().trim());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            UserDTO registeredUser = userService.register(userDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.OK.name());
            response.put("statusMessage", "User is successfully registered");
            response.put("data", registeredUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.INVALID_DATA.name());
            response.put("statusMessage", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PatchMapping("/{username}")
    @PreAuthorize("hasAuthority('ADMIN_UPDATE')")
    public ResponseEntity<?> updateUser(@PathVariable String username, @Valid @RequestBody UserDTO userDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errorMessage.append(fieldError.getDefaultMessage()).append(" ");
            }
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.INVALID_DATA.name());
            response.put("statusMessage", errorMessage.toString().trim());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            UserDTO updatedUser = userService.updateUser(username, userDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.OK.name());
            response.put("statusMessage", "User is successfully updated");
            response.put("data", updatedUser);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.INVALID_DATA.name());
            response.put("statusMessage", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        try {
            UserDTO userDTO = userService.getUserByUsername(username);
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.OK.name());
            response.put("statusMessage", "User was obtained successfully");
            response.put("data", userDTO);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException ex) {
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.NOT_FOUND.name());
            response.put("statusMessage", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalStateException ex) {
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.BAD_REQUEST.name());
            response.put("statusMessage", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    @PatchMapping("/{username}/balance")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<?> updateBalance(@PathVariable String username, @RequestParam double amount) {
        try {
            UserDTO updatedUser = userService.updateBalance(username, amount);
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.OK.name());
            response.put("statusMessage", "Balance updated successfully");
            response.put("data", updatedUser);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException ex) {
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.NOT_FOUND.name());
            response.put("statusMessage", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalArgumentException ex) {
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.INVALID_DATA.name());
            response.put("statusMessage", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    @GetMapping("/allUsers")
    @PreAuthorize("hasAuthority('ADMIN_READ')")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserDTO> users = userService.getAllUsers();
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.OK.name());
            response.put("statusMessage", "Users obtained successfully");
            response.put("data", users);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.INTERNAL_ERROR.name());
            response.put("statusMessage", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PatchMapping("/changeAccountStatus")
    @PreAuthorize("hasAuthority('ADMIN_UPDATE')")
    public ResponseEntity<?> changeAccountStatus(@RequestBody UserDTO userDTO) {
        try {
            UserDTO updatedUser = userService.changeAccountStatus(userDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.OK.name());
            response.put("statusMessage", "User status updated successfully");
            response.put("data", updatedUser);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException ex) {
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.NOT_FOUND.name());
            response.put("statusMessage", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception ex) {
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.INTERNAL_ERROR.name());
            response.put("statusMessage", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
