package pl.piomin.services.emailreminders.controller.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.piomin.services.emailreminders.dto.request.MagicLinkRequest;
import pl.piomin.services.emailreminders.dto.request.RegisterRequest;
import pl.piomin.services.emailreminders.dto.response.AuthResponse;
import pl.piomin.services.emailreminders.dto.response.UserResponse;
import pl.piomin.services.emailreminders.service.AuthService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse user = authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/magic-link")
    public ResponseEntity<Map<String, String>> requestMagicLink(@Valid @RequestBody MagicLinkRequest request) {
        authService.sendMagicLink(request);
        return ResponseEntity.ok(Map.of("message", "If an account exists, a magic link has been sent"));
    }

    @GetMapping("/verify")
    public ResponseEntity<AuthResponse> verifyMagicLink(@RequestParam String token) {
        AuthResponse response = authService.authenticateWithMagicLink(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }
}
