package com.project.backend.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.project.backend.entity.Const;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {
    @Value("${spring.security.jwt.key}")
    String key;
    @Value("${spring.security.jwt.expire}")
    int expire;

    @Resource
    StringRedisTemplate template;
    
    public String createJwt(UserDetails userDetails, int id, String username) {
    // Define the algorithm used for signing the JWT
    Algorithm algorithm = Algorithm.HMAC256(key);

    // Generate the expiration date for the JWT
    Date expireDate = this.expireDate();

    // Create and sign the JWT with claims (id, username, authorities)
    return JWT.create()
            .withJWTId(UUID.randomUUID().toString()) // Add a unique identifier to the JWT
            .withClaim("id", id) // Add the user ID as a claim
            .withClaim("name", username) // Add the username as a claim
            .withClaim("authorities", userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).toList()) // Add authorities as a claim
            .withExpiresAt(expireDate) // Set the expiration date
            .withIssuedAt(new Date()) // Set the issued date
            .sign(algorithm); // Sign the JWT with the algorithm
    }
    
    public DecodedJWT resolveJwt(String bearToken) {
        // Strip the "Bearer" prefix from the token
        String token = stripToken(bearToken);
        if (token == null) return null;
    
        // Define the algorithm used for verifying the JWT
        Algorithm algorithm = Algorithm.HMAC256(key);
    
        // Build a JWT verifier
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
    
        // Verify the token and check its validity
        try {
            DecodedJWT verify = jwtVerifier.verify(token);
            if (this.isTokenInvalid(verify.getId())) return null; // Check if the token is invalid
    
            // Check if the token is expired
            Date expireDate = verify.getExpiresAt();
            return new Date().before(expireDate) ? verify : null;
    
        } catch (JWTVerificationException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean invalidateJwt(String bearToken) {
        // Strip the "Bearer" prefix from the token
        String token = stripToken(bearToken);
        if (token == null) return false;
    
        // Define the algorithm used for verifying the JWT
        Algorithm algorithm = Algorithm.HMAC256(key);
    
        // Build a JWT verifier
        JWTVerifier verifier = JWT.require(algorithm).build();
    
        // Verify and invalidate the token by adding its ID to the blacklist
        try {
            DecodedJWT decodedJWT = verifier.verify(token);
            String id = decodedJWT.getId();
            return deleteToken(id, decodedJWT.getExpiresAt());
        } catch (JWTVerificationException e) {
            return false;
        }
    }
    
    public boolean deleteToken(String uuid, Date time) {
        // If the token is already invalid, return false
        if (isTokenInvalid(uuid)) return false;
    
        // Calculate the remaining time until the token expires
        Date now = new Date();
        long expire = Math.max(time.getTime() - now.getTime(), 0);
    
        // Add the token ID to the blacklist with the remaining time until expiration
        template.opsForValue().set(Const.JWT_BLACK_LIST + uuid, "", expire, TimeUnit.MILLISECONDS);
        return true;
    }
    
    public boolean isTokenInvalid(String uuid) {
        // Check if the token ID is in the blacklist
        return Boolean.TRUE.equals(template.hasKey(Const.JWT_BLACK_LIST + uuid));
    }
    
    public Date expireDate() {
        // Set the expiration date to a specified number of days from now
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, expire * 24);
        return calendar.getTime();
    }
    
    private String stripToken(String token) {
        // Remove the "Bearer" prefix from the token if it exists
        if (token == null || !token.startsWith("Bearer"))
            return null;
        return token.substring(7);
    }
    
    public UserDetails toUser(DecodedJWT jwt) {
        // Convert the claims from the JWT to a UserDetails object
        Map<String, Claim> claims = jwt.getClaims();
        return User.withUsername(claims.get("name").asString())
                .password("00000") // Placeholder password (not used)
                .authorities(claims.get("authorities").asArray(String.class))
                .build();
    }
    
    public Integer toID(DecodedJWT jwt) {
        // Extract the user ID from the JWT claims
        Map<String, Claim> claims = jwt.getClaims();
        return claims.get("id").asInt();
    }

}
