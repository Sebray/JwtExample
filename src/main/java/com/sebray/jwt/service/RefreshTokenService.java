package com.sebray.jwt.service;

import com.sebray.jwt.entity.RefreshToken;
import com.sebray.jwt.entity.User;
import com.sebray.jwt.repository.RefreshTokenRepository;
import com.sebray.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void addRefreshToken(User user, String token){
        refreshTokenRepository.save(new RefreshToken(null, user, token));
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public String getTokenByUsername(String username) {
        return refreshTokenRepository.findByUser_Username(username).get().getToken();
    }

    @Transactional
    public int deleteByUsername(String username) {
        return refreshTokenRepository.deleteByUser_Username(username);
    }
}
