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
    public String createJwt(UserDetails userDetails, int id, String username){
        Algorithm algorithm=Algorithm.HMAC256(key);
        Date expireDate=this.expireDate();
        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withClaim("id",id)
                .withClaim("name",username)
                .withClaim("authorities",userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withExpiresAt(expireDate)
                .withIssuedAt(new Date())
                .sign(algorithm);
    }
    public DecodedJWT resolveJwt(String bearToken) {
        String token=stripToken(bearToken);
        if(token==null) return null;
        Algorithm algorithm=Algorithm.HMAC256(key);
        //构建verifier
        JWTVerifier jwtVerifier=JWT.require(algorithm).build();
        //判断token是否合法，比如是否被篡改过
        try{
            DecodedJWT verify = jwtVerifier.verify(token);
            if(this.isTokenInvalid(verify.getId())) return null;
            //判断token是否expired
            Date expireDate=verify.getExpiresAt();
            return new Date().before(expireDate)?verify:null;

        }catch (JWTVerificationException e){
            e.printStackTrace();
            return null;
        }
    }

    public boolean invalidateJwt(String bearToken) {
        String token=stripToken(bearToken);
        if(token==null) return false;
        Algorithm algorithm=Algorithm.HMAC256(key);
        JWTVerifier verifier=JWT.require(algorithm).build();
        try{
            DecodedJWT decodedJWT = verifier.verify(token);
            String id= decodedJWT.getId();
            return deleteToken(id,decodedJWT.getExpiresAt());
        }catch (JWTVerificationException e){
            return false;
        }
    }

    public boolean deleteToken(String uuid,Date time){
        if(isTokenInvalid(uuid)) return false;
        Date now=new Date();
        long expire=Math.max(time.getTime()- now.getTime(),0);
        template.opsForValue().set(Const.JWT_BLACK_LIST+uuid,"",expire, TimeUnit.MILLISECONDS);
        return true;
    }

    public boolean isTokenInvalid(String uuid){
        //如果黑名单里有token的id，表示token已失效
        return Boolean.TRUE.equals(template.hasKey(Const.JWT_BLACK_LIST+uuid));
    }

    public Date expireDate(){
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.HOUR,expire*24);
        return calendar.getTime();
    }

    private String stripToken(String token){
        if(token==null || !token.startsWith("Bearer"))
            return null;
        return token.substring(7);
    }


    public UserDetails toUser(DecodedJWT jwt) {
        Map<String, Claim> claims = jwt.getClaims();
        return User.withUsername(claims.get("name").asString())
                .password("00000")
                .authorities(claims.get("authorities").asArray(String.class)).build();
    }

    public Integer toID(DecodedJWT jwt) {
        Map<String, Claim> claims = jwt.getClaims();
        return claims.get("id").asInt();
    }
}
