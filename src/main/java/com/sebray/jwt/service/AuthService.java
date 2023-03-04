package com.sebray.jwt.service;

import com.sebray.jwt.dto.LoginDto;
import com.sebray.jwt.dto.UserDto;
import com.sebray.jwt.entity.RefreshToken;
import com.sebray.jwt.entity.Role;
import com.sebray.jwt.entity.User;
import com.sebray.jwt.exception.AuthException;
import com.sebray.jwt.exception.ResourceAlreadyExistsException;
import com.sebray.jwt.exception.ResourceNotFoundException;
import com.sebray.jwt.repository.RefreshTokenRepository;
import com.sebray.jwt.repository.RoleRepository;
import com.sebray.jwt.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public LoginDto signUp(UserDto userDto){
        if (userRepository.existsByUsername(userDto.getUsername()))
            throw new ResourceAlreadyExistsException("The username exists");

        if (userRepository.existsByEmail(userDto.getEmail()))
            throw new ResourceAlreadyExistsException("The email exists");

        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(userDto.getRole())
                .orElseThrow(() -> new ResourceNotFoundException("Role does not exist")));

        User user = new User(null,
                userDto.getUsername(),
                userDto.getEmail(),
                passwordEncoder.encode(userDto.getPassword()),
                false,
                UUID.randomUUID().toString(),
                roles,
                null);
        userRepository.save(user);
        return new LoginDto(userDto.getUsername(), userDto.getPassword());
    }

    public LoginDto activateUser(UserDto userDto){
        User user = userRepository.findByUsername(userDto.getUsername())
                .orElseThrow(()-> new ResourceNotFoundException("The user doesn't exist"));
//        if (!user.getEmail().equals(userDto.getEmail()))
//            throw new несоответствие значений

//        if(!user.getActivationCode().equals(userDto.getActivationCode()))
//
        user.setIsEnable(true);
        userRepository.save(user);
        return new LoginDto(user.getUsername(), userDto.getPassword());
    }

    @Transactional
    public Map<String, String> login(@NonNull LoginDto authRequest) {
        final User user = userRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(() -> new AuthException("User not found"));

        if (passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            final ResponseCookie accessCookie = jwtProvider.generateJwtCookie(user);
            final ResponseCookie refreshCookie = jwtProvider.generateRefreshJwtCookie(user);
            refreshTokenRepository.deleteByUser_Username(user.getUsername());
            refreshTokenRepository.save(new RefreshToken(null, user, refreshCookie.getValue()));
            return Map.of("access", accessCookie.toString(), "refresh", refreshCookie.toString());
        } else {
            throw new AuthException("Incorrect login or password");
        }
    }

    @Transactional
    public Map<String, String> logout(String username) {
        if(!userRepository.existsByUsername(username))
            throw new AuthException("User not found");
        refreshTokenRepository.deleteByUser_Username(username);

        return Map.of("access", jwtProvider.getCleanJwtCookie().toString(),
                "refresh", jwtProvider.getCleanJwtRefreshCookie().toString());
    }

    public String getAccessToken(String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String username = claims.getSubject();
            final String saveRefreshToken = refreshTokenRepository
                    .findByUser_Username((username))
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"))
                    .getToken();

            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new AuthException("User not found"));
                return jwtProvider.generateJwtCookie(user).toString();
            }
        }
        return null;
    }

    @Transactional
    public Map<String, String> refresh(@NonNull String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String username = claims.getSubject();
            final String saveRefreshToken = refreshTokenRepository
                    .findByUser_Username((username))
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"))
                    .getToken();
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new AuthException("User not found"));
                final String accessToken = jwtProvider.generateJwtCookie(user).toString();
                final String newRefreshToken = jwtProvider.generateRefreshJwtCookie(user).toString();

                refreshTokenRepository.deleteByUser_Username(username);
                refreshTokenRepository.save(new RefreshToken(null, user, newRefreshToken));
                return Map.of("access", accessToken, "refresh", newRefreshToken);
            }
        }
        throw new AuthException("Invalid JWT token");
    }
}
