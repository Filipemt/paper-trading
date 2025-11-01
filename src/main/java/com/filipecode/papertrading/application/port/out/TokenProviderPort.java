package com.filipecode.papertrading.application.port.out;

import com.filipecode.papertrading.domain.model.user.User;

public interface TokenProviderPort {
    String generateToken(User user);
    String validateToken(String token);
}
