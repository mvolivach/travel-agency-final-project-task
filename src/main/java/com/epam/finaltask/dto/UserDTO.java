package com.epam.finaltask.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private UUID id;
    @NotBlank(message = "Username cannot be blank")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username must contain only characters and numbers")
    private String username;
    @NotBlank(message = "Password cannot be blank")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).*$",
            message = "")
    @Size(min = 7, max = 30, message = "Your password must contain upper and lower case letters and numbers, at least 7 and maximum 30 characters.Password cannot contains spaces")
    private String password;
    private String role;
    private boolean accountStatus;
    private double balance;
    @Pattern(regexp = "\\+?[0-9]{7,15}",
            message = "Phone number must contain only numbers")
    private String phoneNumber;

}
