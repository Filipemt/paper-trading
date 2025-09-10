package com.filipecode.papertrading.domain.service;

import com.filipecode.papertrading.domain.model.user.User;

public interface TokenProviderPort {
    String generateToken(User user);
}
