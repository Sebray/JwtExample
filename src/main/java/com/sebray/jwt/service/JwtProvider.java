package com.sebray.jwt.service;

import com.sebray.jwt.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

    @Value("${jwt.secret.access}")
    private String jwtAccessSecret;
    @Value("${jwt.secret.refresh}")
    private String jwtRefreshSecret;
    @Value("${bezkoder.app.jwtCookieName}")
    private String jwtCookie;
    @Value("${bezkoder.app.jwtRefreshCookieName}")
    private String jwtRefreshCookie;

    public ResponseCookie generateJwtCookie(@NonNull User user) {
        String jwt = generateAccessToken(user);
        return generateCookie(jwtCookie, jwt, "/api");
    }

    public ResponseCookie generateRefreshJwtCookie(@NonNull User user) {
        String jwt = generateRefreshToken(user);
        return generateCookie(jwtRefreshCookie, jwt, "/api/auth");
    }

    public boolean validateAccessToken(@NonNull String accessToken) {
        return validateToken(accessToken, getSigningKey(jwtAccessSecret));
    }

    public boolean validateRefreshToken(@NonNull String refreshToken) {
        return validateToken(refreshToken, getSigningKey(jwtRefreshSecret));
    }

    public Claims getRefreshClaims(@NonNull String token) {
        return getClaims(token, getSigningKey(jwtRefreshSecret));
    }

    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookie, null).path("/api").build();
    }

    public ResponseCookie getCleanJwtRefreshCookie() {
        return ResponseCookie.from(jwtRefreshCookie, null).path("/api/auth").build();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtAccessSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);

        if (cookie != null) {
            return cookie.getValue();
        }

        return null;
    }

    public String getJwtFromRefreshCookie(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtRefreshCookie);

        if (cookie != null) {
            return cookie.getValue();
        }

        return null;
    }

    private String generateAccessToken(@NonNull User user) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant accessExpirationInstant = now.plusHours(2).atZone(ZoneId.systemDefault()).toInstant();
        final Date accessExpiration = Date.from(accessExpirationInstant);
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setExpiration(accessExpiration)
                .signWith(SignatureAlgorithm.HS512, jwtAccessSecret)
                .claim("roles", user.getRoles())
                .claim("email", user.getEmail())
                .compact();
    }

    private String generateRefreshToken(@NonNull User user) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant refreshExpirationInstant = now.plusDays(30).atZone(ZoneId.systemDefault()).toInstant();
        final Date refreshExpiration = Date.from(refreshExpirationInstant);
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setExpiration(refreshExpiration)
                .signWith(SignatureAlgorithm.HS512, jwtRefreshSecret)
                .compact();
    }

    private boolean validateToken(@NonNull String token, @NonNull Key secret) {
        try {
            Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Token expired", expEx);
        } catch (UnsupportedJwtException unsEx) {
            log.error("Unsupported jwt", unsEx);
        } catch (MalformedJwtException mjEx) {
            log.error("Malformed jwt", mjEx);
        } catch (SignatureException sEx) {
            log.error("Invalid signature", sEx);
        } catch (Exception e) {
            log.error("invalid token", e);
        }
        return false;
    }

    private Key getSigningKey(String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims getClaims(@NonNull String token, @NonNull Key secret) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    private ResponseCookie generateCookie(String name, String value, String path) {
        return ResponseCookie.from(name, value).path(path).maxAge(15 * 24 * 60 * 60).httpOnly(true).build();
    }
}