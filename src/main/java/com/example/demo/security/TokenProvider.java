package com.example.demo.security;

import com.example.demo.model.UserEntity;
import com.example.demo.persistence.UserRepository;
import com.example.demo.security.oauth.ApplicationOAuth2User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
public class TokenProvider {
    @Autowired
    private UserRepository userRepository;

    private static final String SECRET_KEY="asndbhabrgnfhbdshfuenfghgkeoqlamffhudkwlsnfjsjkwlfiemtbnsnzhrb" +
            "sdsdRfndfFsSSEergFsdRsSwqsafFddfbnmgnfmzsienrla";


    //생성
    public String create(UserEntity userEntity) {
        // 기한 지금으로부터 1일로 설정
        Date expiryDate = Date.from(
                Instant.now()
                        .plus(1, ChronoUnit.DAYS));
        // JWT Token 생성
        return Jwts.builder()
                // header에 들어갈 내용 및 서명을 하기 위한 SECRET_KEY
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                // payload에 들어갈 내용
                .setSubject(userEntity.getId()) // sub
                .setIssuer("demo app") // iss
                .setIssuedAt(new Date()) // iat
                .setExpiration(expiryDate) // exp
                .compact();
    }

    public String create(final Authentication authentication) {
        ApplicationOAuth2User userPrincipal = (ApplicationOAuth2User) authentication.getPrincipal();
        Date expiryDate = Date.from(
                Instant.now()
                        .plus(1, ChronoUnit.DAYS));

        return Jwts.builder()
                .setSubject(userPrincipal.getName()) // id가 리턴됨.
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    //refresh 토큰 생성
    public String createRefreshToken(UserEntity userEntity){
        Date expiryDate=Date.from(Instant.now()
                .plus(14,ChronoUnit.DAYS)); // 14일을 유효기간으로

        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512,SECRET_KEY)
                //.setSubject(userEntity.getId())
                .setIssuer("demo app")
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .compact();
    }

    //검증
    public String validateAndGetUserId(String token){
        Claims claims=Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    //리프레시 토큰 설정
    @Transactional
    public void setRefreshToken(String userName,String refreshToken){
        UserEntity user = userRepository.findByUsername(userName);
        if(user!=null){
            user.setRefreshToken(refreshToken);
        }
    }


}
