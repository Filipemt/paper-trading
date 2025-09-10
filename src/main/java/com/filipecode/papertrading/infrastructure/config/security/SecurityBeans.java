package com.filipecode.papertrading.infrastructure.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityBeans {

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
