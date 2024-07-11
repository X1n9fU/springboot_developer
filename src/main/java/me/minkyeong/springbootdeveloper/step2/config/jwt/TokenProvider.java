package me.minkyeong.springbootdeveloper.step2.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import me.minkyeong.springbootdeveloper.step2.domain.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@Service
public class TokenProvider {

    private final JwtProperties jwtProperties;

    private final Key key;

    public TokenProvider(JwtProperties jwtProperties){
        this.jwtProperties = jwtProperties;
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User user, Duration expiredAt){
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }

    //JWT 토큰 생성 메서드
    private String makeToken(Date expiry, User user) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) //헤더 typ : JWT
                .setIssuer(jwtProperties.getIssuer()) //내용 iss : yml 파일 설정 값
                .setIssuedAt(now) //내용 iat : 현재 시간
                .setExpiration(expiry) //내용 exp : 현재 시간 + 유효 시간
                .setSubject(user.getEmail()) //내용 sub : 유저의 email
                .claim("id", user.getId()) //클레임 id : 유저 ID
                .signWith(key, SignatureAlgorithm.HS256) //서명 : 비밀값과 함께 해시값을 HS256 방식으로 암호화
                .compact();
    }

    //JWT 토큰 유효성 검증 메서드
    public boolean validToken(String token){
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key) //비밀값으로 복호화
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e){
            return false; //복호화 과정에서 에러가 나면 유효하지 않은 토큰
        }
    }

    //토큰 기반으로 인증 정보를 가져오는 메서드
    public Authentication getAuthentication(String token){
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority("ROLE_USER")
        );

        return new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(
                        claims.getSubject(), "", authorities),
                token, authorities
        );
    }

    //토큰 기반으로 유저 ID를 가져오는 메서드
    public Long getUserId(String token){
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    private Claims getClaims(String token){
        return Jwts.parserBuilder() //클레임 조회
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
