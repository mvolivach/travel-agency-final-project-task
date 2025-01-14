package com.epam.finaltask.controllers;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.models.Role;
import com.epam.finaltask.models.User;
import com.epam.finaltask.auth.request.LoginRequest;
import com.epam.finaltask.auth.response.JwtResponse;
import com.epam.finaltask.auth.response.MessageResponse;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.token.JwtUtils;
import com.epam.finaltask.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {

  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    List<String> permissions = userDetails.getAuthorities().stream()
            .map(item -> item.getAuthority())
            .collect(Collectors.toList());

    UserDTO userDTO = UserDTO.builder()
            .id(userDetails.getId())
            .username(userDetails.getUsername())
            .phoneNumber(userDetails.getPhoneNumber())
            .role(permissions.isEmpty() ? "USER" : permissions.get(0))
            .accountStatus(true)
            .balance(0.0)
            .build();

    return ResponseEntity.ok(new JwtResponse(jwt,
            userDetails.getId(),
            userDetails.getUsername(),
            userDetails.getPhoneNumber(),
            permissions));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO userDTO) {
    if (userRepository.existsByUsername(userDTO.getUsername())) {
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse("Error: Username is already taken!"));
    }

    if (userRepository.existsByPhoneNumber(userDTO.getPhoneNumber())) {
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse("Error: Phone number is already in use!"));
    }

    User user = new User();
    user.setUsername(userDTO.getUsername());
    user.setPhoneNumber(userDTO.getPhoneNumber());
    user.setPassword(encoder.encode(userDTO.getPassword()));
    user.setBalance(userDTO.getBalance());

    if (userDTO.getRole() != null && !userDTO.getRole().isEmpty()) {
      try {
        user.setRole(Role.valueOf(userDTO.getRole().toUpperCase()));
      } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid role specified!"));
      }
    } else {
      user.setRole(Role.USER);
    }

    userRepository.save(user);

    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }
}
