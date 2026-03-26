package pl.piomin.services.emailreminders.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.piomin.services.emailreminders.dto.request.MagicLinkRequest;
import pl.piomin.services.emailreminders.dto.request.RegisterRequest;
import pl.piomin.services.emailreminders.dto.response.AuthResponse;
import pl.piomin.services.emailreminders.dto.response.UserResponse;
import pl.piomin.services.emailreminders.exception.DuplicateResourceException;
import pl.piomin.services.emailreminders.exception.InvalidTokenException;
import pl.piomin.services.emailreminders.model.MagicLinkToken;
import pl.piomin.services.emailreminders.model.User;
import pl.piomin.services.emailreminders.repository.MagicLinkTokenRepository;
import pl.piomin.services.emailreminders.repository.UserRepository;
import pl.piomin.services.emailreminders.security.JwtService;

import java.time.LocalDateTime;

@Service
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final MagicLinkTokenRepository tokenRepository;
    private final EmailService emailService;
    private final TokenService tokenService;
    private final JwtService jwtService;
    private final int magicLinkExpirationMinutes;

    public AuthService(UserRepository userRepository,
                       MagicLinkTokenRepository tokenRepository,
                       EmailService emailService,
                       TokenService tokenService,
                       JwtService jwtService,
                       @Value("${magic-link.expiration-minutes}") int magicLinkExpirationMinutes) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.tokenService = tokenService;
        this.jwtService = jwtService;
        this.magicLinkExpirationMinutes = magicLinkExpirationMinutes;
    }

    public UserResponse registerUser(RegisterRequest request) {
        String email = request.getEmail().toLowerCase().trim();

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("User", "email", email);
        }

        User user = new User(email, request.getDisplayName());
        User saved = userRepository.save(user);

        // Send magic link for initial login
        sendMagicLinkInternal(saved);

        return UserResponse.fromEntity(saved);
    }

    public void sendMagicLink(MagicLinkRequest request) {
        String email = request.getEmail().toLowerCase().trim();
        User user = userRepository.findByEmail(email).orElse(null);

        // Always return success to prevent email enumeration
        if (user != null && user.getActive()) {
            sendMagicLinkInternal(user);
        } else {
            logger.info("Magic link requested for non-existent or inactive email: {}", email);
        }
    }

    private void sendMagicLinkInternal(User user) {
        String token = tokenService.generateSecureToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(magicLinkExpirationMinutes);

        MagicLinkToken magicLinkToken = new MagicLinkToken(token, user, expiresAt);
        tokenRepository.save(magicLinkToken);

        emailService.sendMagicLinkEmail(user.getEmail(), token);
        logger.info("Magic link sent to: {}", user.getEmail());
    }

    public AuthResponse authenticateWithMagicLink(String token) {
        MagicLinkToken magicLinkToken = tokenRepository
                .findByTokenAndUsedFalseAndExpiresAtAfter(token, LocalDateTime.now())
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired magic link"));

        // Mark token as used
        magicLinkToken.setUsed(true);
        magicLinkToken.setUsedAt(LocalDateTime.now());
        tokenRepository.save(magicLinkToken);

        User user = magicLinkToken.getUser();

        // Mark email as verified on first successful login
        if (!user.getEmailVerified()) {
            user.setEmailVerified(true);
            userRepository.save(user);
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken,
                jwtService.getAccessTokenExpiration() / 1000, // Convert to seconds
                UserResponse.fromEntity(user)
        );
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken) || !jwtService.isRefreshToken(refreshToken)) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        String email = jwtService.extractEmail(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidTokenException("User not found"));

        if (!user.getActive()) {
            throw new InvalidTokenException("User account is inactive");
        }

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(
                newAccessToken,
                newRefreshToken,
                jwtService.getAccessTokenExpiration() / 1000,
                UserResponse.fromEntity(user)
        );
    }

    public User getUserFromMagicLink(String token) {
        MagicLinkToken magicLinkToken = tokenRepository
                .findByTokenAndUsedFalseAndExpiresAtAfter(token, LocalDateTime.now())
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired magic link"));

        // Mark token as used
        magicLinkToken.setUsed(true);
        magicLinkToken.setUsedAt(LocalDateTime.now());
        tokenRepository.save(magicLinkToken);

        User user = magicLinkToken.getUser();

        // Mark email as verified on first successful login
        if (!user.getEmailVerified()) {
            user.setEmailVerified(true);
            userRepository.save(user);
        }

        return user;
    }
}
