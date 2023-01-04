package com.sebray.jwt.repository;

import com.sebray.jwt.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Boolean existsRefreshTokenByToken(String token);
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser_Username(String username);
    int deleteByUser_Username(String username);
}
