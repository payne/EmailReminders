package pl.piomin.services.emailreminders.service;

import org.springframework.stereotype.Service;
import pl.piomin.services.emailreminders.util.TokenGenerator;

@Service
public class TokenService {

    public String generateSecureToken() {
        return TokenGenerator.generateSecureToken();
    }
}
