package pl.piomin.services.emailreminders.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.piomin.services.emailreminders.dto.request.UserUpdateRequest;
import pl.piomin.services.emailreminders.dto.response.UserResponse;
import pl.piomin.services.emailreminders.exception.ResourceNotFoundException;
import pl.piomin.services.emailreminders.model.User;
import pl.piomin.services.emailreminders.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "Test User");
        testUser.setId(1L);
        testUser.setActive(true);
        testUser.setEmailVerified(true);
    }

    @Test
    void findById_shouldReturnUser_whenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserResponse result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getDisplayName(), result.getDisplayName());
    }

    @Test
    void findById_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findById(99L));
    }

    @Test
    void findByEmail_shouldReturnUser_whenUserExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        UserResponse result = userService.findByEmail("test@example.com");

        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
    }

    @Test
    void update_shouldUpdateDisplayName() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserUpdateRequest request = new UserUpdateRequest("New Name");
        UserResponse result = userService.update(1L, request);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void delete_shouldDeactivateUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.delete(1L);

        verify(userRepository).save(any(User.class));
    }
}
