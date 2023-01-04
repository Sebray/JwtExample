package com.sebray.jwt.controller;

import com.sebray.jwt.dto.LoginDto;
import com.sebray.jwt.dto.UserDto;
import com.sebray.jwt.service.AuthService;
import com.sebray.jwt.service.JwtProvider;
import com.sebray.jwt.service.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtProvider jwtProvider;

    @PostMapping("/register")
    public ResponseEntity<?> signUp(UserDto userDto) {
        LoginDto loginData = authService.SignUp(userDto);
        final Map<String, String> cookies = authService.login(loginData);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookies.get("access"))
                .header(HttpHeaders.SET_COOKIE, cookies.get("refresh"))
                .body("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto user) {
        final Map<String, String> cookies = authService.login(user);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookies.get("access"))
                .header(HttpHeaders.SET_COOKIE, cookies.get("refresh"))
                .body("User successfully logged in!");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final Map<String, String> cookies = authService.logout(user.getUsername());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookies.get("access"))
                .header(HttpHeaders.SET_COOKIE, cookies.get("refresh"))
                .body("Jwt dropped");
    }

    @PostMapping("/token")
    public ResponseEntity<?> getNewAccessToken(HttpServletRequest request) {
        String refreshToken = jwtProvider.getJwtFromRefreshCookie(request);
        final String token = authService.getAccessToken(refreshToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, token)
                .body("Access token received");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> getNewRefreshToken(HttpServletRequest request) {
        String refreshToken = jwtProvider.getJwtFromRefreshCookie(request);
        final Map<String, String> cookies = authService.refresh(refreshToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookies.get("access"))
                .header(HttpHeaders.SET_COOKIE, cookies.get("refresh"))
                .body("Refresh and access tokens updated");
    }
}
