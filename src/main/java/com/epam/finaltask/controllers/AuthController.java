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
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder encoder;

  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private MessageSource messageSource;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
                                            @RequestHeader(name = "Accept-Language", required = false) String locale) {
    logger.info("Received sign-in request for username: {}", loginRequest.getUsername());
    User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);

    if (user == null) {
      logger.warn("Authentication failed: user not found for username: {}", loginRequest.getUsername());
      return ResponseEntity.badRequest().body(new MessageResponse(
              messageSource.getMessage("auth.invalid_credentials", null, getLocale(locale))));
    }

    if (!user.isAccountStatus()) {
      logger.warn("Authentication failed: account is blocked for username: {}", loginRequest.getUsername());
      return ResponseEntity.badRequest().body(new MessageResponse(
              messageSource.getMessage("auth.account_blocked", null, getLocale(locale))));
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

      logger.info("User authenticated successfully: {}", userDetails.getUsername());
      return ResponseEntity.ok(new JwtResponse(jwt,
              userDetails.getId(),
              userDetails.getUsername(),
              userDetails.getPhoneNumber(),
              permissions));
    } catch (Exception e) {
      logger.error("Authentication failed for username: {}. Error: {}", loginRequest.getUsername(), e.getMessage());
      return ResponseEntity.badRequest().body(new MessageResponse(
              messageSource.getMessage("auth.invalid_credentials", null, getLocale(locale))));
    }
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult,
                                        @RequestHeader(name = "Accept-Language", required = false) String locale) {
    logger.info("Received sign-up request for username: {}", userDTO.getUsername());
    Locale userLocale = getLocale(locale);

    if (bindingResult.hasErrors()) {
      logger.warn("Sign-up validation failed for username: {}", userDTO.getUsername());
      List<String> errors = bindingResult.getFieldErrors().stream()
              .map(fieldError -> {
                switch (fieldError.getField()) {
                  case "username":
                    return messageSource.getMessage("username.invalid", null, userLocale);
                  case "password":
                    return messageSource.getMessage("password.invalid", null, userLocale);
                  case "phoneNumber":
                    return messageSource.getMessage("phoneNumber.invalid", null, userLocale);
                  default:
                    return messageSource.getMessage("field.invalid", null, userLocale);
                }
              })
              .collect(Collectors.toList());
      return ResponseEntity.badRequest().body(Map.of("errors", errors));
    }

    if (userRepository.existsByUsername(userDTO.getUsername())) {
      logger.warn("Sign-up failed: username already taken: {}", userDTO.getUsername());
      return ResponseEntity
              .badRequest()
              .body(Map.of("errors", List.of(
                      messageSource.getMessage("username.taken", null, userLocale))));
    }

    if (userRepository.existsByPhoneNumber(userDTO.getPhoneNumber())) {
      logger.warn("Sign-up failed: phone number already in use: {}", userDTO.getPhoneNumber());
      return ResponseEntity
              .badRequest()
              .body(Map.of("errors", List.of(
                      messageSource.getMessage("phoneNumber.in.use", null, userLocale))));
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
        logger.warn("Sign-up failed: invalid role provided: {}", userDTO.getRole());
        return ResponseEntity.badRequest().body(Map.of("errors", List.of(
                messageSource.getMessage("role.invalid", null, userLocale))));
      }
    } else {
      user.setRole(Role.USER);
    }

    userRepository.save(user);
    logger.info("User registered successfully: {}", userDTO.getUsername());

    return ResponseEntity.ok(new MessageResponse(
            messageSource.getMessage("user.registered.success", null, userLocale)));
  }

  private Locale getLocale(String locale) {
    return locale != null && !locale.isEmpty() ? Locale.forLanguageTag(locale) : Locale.forLanguageTag("uk");
  }
}
