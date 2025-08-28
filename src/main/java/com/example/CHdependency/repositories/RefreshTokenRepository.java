package com.example.CHdependency.repositories;

import com.example.CHdependency.entities.RefreshToken;
import com.example.CHdependency.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Transactional
    void deleteByUserId(Long userId);

    RefreshToken findByUserId(Long userId);
    Optional<RefreshToken> findAllByUserId(Long userId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RefreshToken> findByUser(User user);
}