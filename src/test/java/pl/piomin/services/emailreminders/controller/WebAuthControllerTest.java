package pl.piomin.services.emailreminders.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(properties = "management.health.mail.enabled=false")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WebAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JavaMailSender mailSender;

    @Test
    void loginPage_shouldReturnLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    void registerPage_shouldReturnRegisterView() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"));
    }

    @Test
    void checkEmailPage_shouldReturnCheckEmailView() throws Exception {
        mockMvc.perform(get("/check-email"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/check-email"));
    }

    @Test
    void register_shouldRedirectToCheckEmail_onSuccess() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("email", "newuser789@example.com")
                        .param("displayName", "New User"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/check-email"));
    }

    @Test
    void verify_shouldRedirectToLogin_whenTokenInvalid() throws Exception {
        mockMvc.perform(get("/auth/verify")
                        .param("token", "invalid-token"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
