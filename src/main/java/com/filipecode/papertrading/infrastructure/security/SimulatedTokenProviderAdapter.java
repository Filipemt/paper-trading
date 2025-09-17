package com.filipecode.papertrading.infrastructure.security;

import com.filipecode.papertrading.domain.model.user.User;
import com.filipecode.papertrading.domain.service.TokenProviderPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class SimulatedTokenProviderAdapter implements TokenProviderPort {

    private static final String FAKE_TOKEN_PREFIX = "fake-token-for-email:";

    @Override
    public String generateToken(User user) {
        if (user == null || user.getEmail() == null) {
            throw new IllegalArgumentException("User e e-mail não podem ser nulos.");
        }
        return FAKE_TOKEN_PREFIX + user.getEmail();
    }

    @Override
    public String validateToken(String token) {
        if (token != null && token.startsWith(FAKE_TOKEN_PREFIX)) {
            return token.substring(FAKE_TOKEN_PREFIX.length());
        }

        return null;
    }
}
