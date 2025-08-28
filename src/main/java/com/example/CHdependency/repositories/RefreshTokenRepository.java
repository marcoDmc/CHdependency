package com.example.CHdependency.repositories;

import com.example.CHdependency.entities.RefreshToken;
import com.example.CHdependency.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUserId(Long userId);

    RefreshToken findByUserId(Long userId);
    Optional<RefreshToken> findAllByUserId(Long userId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RefreshToken> findByUser(User user);
}