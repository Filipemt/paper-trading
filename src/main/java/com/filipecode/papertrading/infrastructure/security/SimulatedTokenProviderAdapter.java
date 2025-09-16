package com.filipecode.papertrading.infrastructure.security;

import com.filipecode.papertrading.domain.model.user.User;
import com.filipecode.papertrading.domain.service.TokenProviderPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

//@Component
@Profile("dev")
public class SimulatedTokenProviderAdapter implements TokenProviderPort {
    @Override
    public String generateToken(User user) {
        return "fake-jwt-token-for-user-id" + user.getId();
    }

    @Override
    public String validateToken(String token) {
        return "";
    }
}
