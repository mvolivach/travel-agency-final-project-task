package com.epam.finaltask.controllers;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.exception.EntityNotFoundException;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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
    User user = userRepository.findByUsername(loginRequest.getUsername())
            .orElse(null);

    if (user == null) {
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse("Invalid username or password"));
    }

    if (!user.isAccountStatus()) {
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse("Your account is blocked by administrator"));
    }

    try {
      Authentication authentication = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

      SecurityContextHolder.getContext().setAuthentication(authentication);
      String jwt = jwtUtils.generateJwtToken(authentication);

      UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
      List<String> permissions = userDetails.getAuthorities().stream()
              .map(item -> item.getAuthority())
              .collect(Collectors.toList());

      return ResponseEntity.ok(new JwtResponse(jwt,
              userDetails.getId(),
              userDetails.getUsername(),
              userDetails.getPhoneNumber(),
              permissions));
    } catch (Exception e) {
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse("Invalid username or password"));
    }
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      List<String> errors = bindingResult.getFieldErrors().stream()
              .map(error -> error.getDefaultMessage())
              .collect(Collectors.toList());
      return ResponseEntity.badRequest().body(Map.of("errors", errors));
    }

    if (userRepository.existsByUsername(userDTO.getUsername())) {
      return ResponseEntity
              .badRequest()
              .body(Map.of("errors", List.of("Username is already taken!")));
    }

    if (userRepository.existsByPhoneNumber(userDTO.getPhoneNumber())) {
      return ResponseEntity
              .badRequest()
              .body(Map.of("errors", List.of("Phone number is already in use!")));
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
        return ResponseEntity.badRequest().body(Map.of("errors", List.of("Invalid role specified!")));
      }
    } else {
      user.setRole(Role.USER);
    }

    userRepository.save(user);

    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }

}
