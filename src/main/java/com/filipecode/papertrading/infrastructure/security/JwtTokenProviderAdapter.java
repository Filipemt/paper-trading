package com.filipecode.papertrading.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.filipecode.papertrading.domain.model.user.User;
import com.filipecode.papertrading.domain.service.TokenProviderPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Profile("dev")
public class JwtTokenProviderAdapter implements TokenProviderPort {

    private final String secretKey;
    private final Long expirationInMillis;

    public JwtTokenProviderAdapter(@Value("${app.jwt.secret}") String secretKey,
                                   @Value("${app.jwt.expiration-in-ms}") Long expirationInMillis) {
        this.secretKey = secretKey;
        this.expirationInMillis = expirationInMillis;
    }

    @Override
    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.secretKey);

            Instant expiryDate = Instant.now().plusMillis(this.expirationInMillis);

            return JWT.create()
                    .withIssuer("paper-trading-api")
                    .withSubject(user.getEmail())
                    .withClaim("userId", user.getId())
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(expiryDate)
                    .sign(algorithm);

        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error enquanto gera token", exception);
        }
    }

    @Override
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.secretKey);
            return JWT.require(algorithm)
                    .withIssuer("paper-trading-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token inválido ou expirado");
        }
    }

}
