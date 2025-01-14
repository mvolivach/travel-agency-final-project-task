package com.epam.finaltask.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
@Builder
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "phoneNumber")
        })
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotBlank
  @Size(max = 20)
  private String username;

  @NotBlank
  @Size(max = 120)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(name = "role")
  private Role role;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<Voucher> vouchers = new ArrayList<>();

  @NotBlank
  @Size(max = 50)
  private String phoneNumber;

  private double balance;

  private boolean accountStatus = true;

  public User() {
  }

  public User(UUID id, String username, String password, Role role, List<Voucher> vouchers, String phoneNumber, double balance, boolean accountStatus) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.role = role;
    this.vouchers = vouchers;
    this.phoneNumber = phoneNumber;
    this.balance = balance;
    this.accountStatus = accountStatus;
  }

}
