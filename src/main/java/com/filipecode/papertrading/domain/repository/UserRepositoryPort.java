package com.filipecode.papertrading.domain.repository;

import com.filipecode.papertrading.domain.model.user.User;

import java.util.Optional;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByCpf(String cpf);

    void deleteById(Long id);
}
