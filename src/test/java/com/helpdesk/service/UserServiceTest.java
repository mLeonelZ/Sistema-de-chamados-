package com.helpdesk.service;

import com.helpdesk.exception.BusinessRuleException;
import com.helpdesk.exception.ResourceNotFoundException;
import com.helpdesk.model.User;
import com.helpdesk.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void saveShouldHashPassword() {
        User user = new User();
        user.setEmail("u@a.com");
        user.setPassword("12345678");
        when(userRepository.existsByEmail("u@a.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        User saved = userService.save(user);
        assertNotEquals("12345678", saved.getPassword());
    }

    @Test
    void saveShouldThrowWhenEmailAlreadyExists() {
        User user = new User();
        user.setEmail("u@a.com");
        user.setPassword("12345678");
        when(userRepository.existsByEmail("u@a.com")).thenReturn(true);
        assertThrows(BusinessRuleException.class, () -> userService.save(user));
    }

    @Test
    void findByIdShouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.findById(id));
    }

    @Test
    void updateShouldKeepExistingHashWhenPasswordAlreadyMatches() {
        UUID id = UUID.randomUUID();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("12345678");
        User existing = new User();
        existing.setId(id);
        existing.setEmail("a@a.com");
        existing.setPassword(hash);
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));
        User incoming = new User();
        incoming.setEmail("a@a.com");
        incoming.setPassword("12345678");
        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmailAndIdNot("a@a.com", id)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        User saved = userService.update(id, incoming);
        assertEquals(hash, saved.getPassword());
        assertEquals(id, saved.getId());
    }

    @Test
    void updateShouldThrowWhenEmailAlreadyExistsOnAnotherUser() {
        UUID id = UUID.randomUUID();
        User existing = new User();
        existing.setId(id);
        existing.setEmail("a@a.com");
        existing.setPassword("hash");
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));
        User incoming = new User();
        incoming.setEmail("x@x.com");
        incoming.setPassword("12345678");
        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmailAndIdNot("x@x.com", id)).thenReturn(true);
        assertThrows(BusinessRuleException.class, () -> userService.update(id, incoming));
    }
}
