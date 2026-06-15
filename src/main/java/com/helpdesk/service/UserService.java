package com.helpdesk.service;

import com.helpdesk.exception.BusinessRuleException;
import com.helpdesk.exception.ResourceNotFoundException;
import com.helpdesk.exception.UnauthorizedException;
import com.helpdesk.model.User;
import com.helpdesk.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User authenticate(String email, String rawPassword) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UnauthorizedException("Credenciais inválidas."));
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new UnauthorizedException("Credenciais inválidas.");
        }
        if (user.getStatus() != com.helpdesk.model.enums.UserStatus.ACTIVE) {
            throw new UnauthorizedException("Usuário inativo.");
        }
        return user;
    }

    public User save(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BusinessRuleException("Já existe um usuário cadastrado com o e-mail informado.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User update(UUID id, User user) {
        User existing = findById(id);
        if (userRepository.existsByEmailAndIdNot(user.getEmail(), id)) {
            throw new BusinessRuleException("Já existe um usuário cadastrado com o e-mail informado.");
        }
        user.setId(existing.getId());
        user.setCreatedAt(existing.getCreatedAt());
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            user.setPassword(existing.getPassword());
        } else if (passwordEncoder.matches(user.getPassword(), existing.getPassword())) {
            user.setPassword(existing.getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }
}
