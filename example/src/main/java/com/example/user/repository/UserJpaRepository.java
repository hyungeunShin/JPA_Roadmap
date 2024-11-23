package com.example.user.repository;

import com.example.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByNameAndEmail(String name, String email);

    Optional<User> findByUsernameAndNameAndEmail(String username, String name, String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByIdNotAndEmail(Long id, String email);
}
