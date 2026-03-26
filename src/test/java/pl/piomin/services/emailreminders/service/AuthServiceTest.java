package pl.piomin.services.emailreminders.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.mail.MailHealthContributorAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import pl.piomin.services.emailreminders.dto.request.RegisterRequest;
import pl.piomin.services.emailreminders.dto.response.UserResponse;
import pl.piomin.services.emailreminders.exception.DuplicateResourceException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = "management.health.mail.enabled=false")
@ActiveProfiles("test")
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @MockBean
    private JavaMailSender mailSender;

    @Test
    void registerUser_shouldCreateNewUser() {
        RegisterRequest request = new RegisterRequest("newuser@example.com", "New User");

        UserResponse result = authService.registerUser(request);

        assertNotNull(result);
        assertEquals("newuser@example.com", result.getEmail());
        assertEquals("New User", result.getDisplayName());
    }

    @Test
    void registerUser_shouldThrowException_whenEmailExists() {
        // First registration
        RegisterRequest request1 = new RegisterRequest("duplicate@example.com", "First User");
        authService.registerUser(request1);

        // Second registration with same email should fail
        RegisterRequest request2 = new RegisterRequest("duplicate@example.com", "Second User");
        assertThrows(DuplicateResourceException.class, () -> authService.registerUser(request2));
    }
}
