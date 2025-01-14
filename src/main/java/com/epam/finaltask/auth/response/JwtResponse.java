package com.epam.finaltask.auth.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class JwtResponse {
  private String token;
  private String type = "Bearer";
  private UUID id;
  private String username;
  private String phoneNumber;
  private List<String> roles;

  public JwtResponse(String accessToken, UUID id, String username, String phoneNumber, List<String> roles) {
    this.token = accessToken;
    this.id = id;
    this.username = username;
    this.phoneNumber = phoneNumber;
    this.roles = roles;
  }
}
