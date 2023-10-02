package com.user_service.security;

import com.user_service.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LogManager.getLogger();

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs}")
    private Long jwtExpirationInMs;

    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String generateToken(Authentication authentication) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSecretKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String extractUsername(String bearerToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(getJwtFromBearerToken(bearerToken))
                    .getBody();
            return claims.getSubject();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    public String getJwtFromBearerToken(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            final int INDEX_OF_JWT = 7;
            return bearerToken.substring(INDEX_OF_JWT);
        }
        return null;
    }

    public boolean isValidToken(String bearerToken) throws InvalidTokenException {
        String token = getJwtFromBearerToken(bearerToken);
        try {
            Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            logger.error(ex.getMessage());
            String username = extractUsername(bearerToken);
            UserContextHolder.getInstance().removeBearerToken(username, token);
            throw new InvalidTokenException("Jwt Token has expired. Please log in again!");
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return false;
        }
    }

}
