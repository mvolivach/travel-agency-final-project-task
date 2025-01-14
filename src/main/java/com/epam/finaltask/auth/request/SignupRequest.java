package com.epam.finaltask.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class SignupRequest {
  @NotBlank
  @Size(min = 3, max = 20)
  private String username;

  @NotBlank
  @Size(max = 50)
  private String phoneNumber;

  private Set<String> role;

  @NotBlank
  @Size(min = 6, max = 40)
  private String password;

}
