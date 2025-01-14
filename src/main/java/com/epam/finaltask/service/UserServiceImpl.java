package com.epam.finaltask.service;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.exception.EntityAlreadyExistsException;
import com.epam.finaltask.exception.EntityNotFoundException;
import com.epam.finaltask.exception.StatusCodes;
import com.epam.finaltask.mapper.UserMapper;
import com.epam.finaltask.models.User;
import com.epam.finaltask.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO register(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new EntityAlreadyExistsException("This username already exists", StatusCodes.DUPLICATE_USERNAME);
        }
        User userModel = userMapper.toUser(userDTO);
        userModel.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        User savedUser = userRepository.save(userModel);
        return userMapper.toUserDTO(savedUser);
    }

    @Override
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        Page<User> usersPage = userRepository.findAll(pageable);
        return usersPage.map(userMapper::toUserDTO);
    }


    @Override
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));

        if (!user.isAccountStatus()) {
            throw new IllegalStateException("User is inactive: " + username);
        }

        return userMapper.toUserDTO(user);
    }

    @Override
    public UserDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        if (!user.isAccountStatus()) {
            throw new IllegalStateException("User is inactive with id: " + id);
        }

        return userMapper.toUserDTO(user);
    }

    @Override
    public UserDTO updateBalance(String username, double amount) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found", StatusCodes.ENTITY_NOT_FOUND));

        double newBalance = user.getBalance() + amount;

        if (newBalance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }

        user.setBalance(newBalance);
        User updatedUser = userRepository.save(user);

        return userMapper.toUserDTO(updatedUser);
    }

    @Override
    @Transactional
    public UserDTO changeAccountStatus(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userDTO.getId()));
        user.setAccountStatus(userDTO.isAccountStatus());
        User savedUser = userRepository.save(user);
        return userMapper.toUserDTO(savedUser);
    }
}