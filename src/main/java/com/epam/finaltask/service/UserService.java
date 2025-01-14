package com.epam.finaltask.service;

import com.epam.finaltask.dto.UserDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDTO register(UserDTO userDTO);

    UserDTO updateBalance(String username, double amount);
    UserDTO getUserByUsername(String username);
    UserDTO changeAccountStatus(UserDTO userDTO);
    UserDTO getUserById(UUID id);
    List<UserDTO> getAllUsers();

}
