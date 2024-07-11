package me.minkyeong.springbootdeveloper.step2.config.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import me.minkyeong.springbootdeveloper.step2.domain.User;
import me.minkyeong.springbootdeveloper.step2.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class TokenProviderTest {
    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProperties jwtProperties;

    private Key key;

    @BeforeEach
    void setUp() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /*
    Given : 토큰에 유저 정보를 추가 하기 위한 테스트 유저 제작
    When : 토큰 제공자의 generateToken() 메서드를 호출해 토큰 제작
    Then : jjwt 라이브러리를 사용해 토큰 복호화, 토큰을 만들 때 claim으로 넣어둔 id 값이
            given 절에서 만든 유저의 id와 일치하는 지
     */

    @DisplayName("generateToken() : 유저 정보와 만료 기간을 전달해 토큰을 만들 수 있다.")
    @Test
    void generateToken(){
        //given
        User testUser = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build()
        );

        //when
        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));

        //then
        Long userId = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);

        assertThat(userId).isEqualTo(testUser.getId());
    }

    /*
    Given : jjwt 라이브러리를 사용해 토큰 생성, 만료된 토큰으로 생성
    When : validToken() 메서드를 호출해 유효한 토큰인지 검증한 뒤 결과값 반환
    Then : 반환값이 false 인 것 확인
     */

    @DisplayName("validToken() : 만료된 토큰인 때에 유효성 검증에 실패한다.")
    @Test
    void validToken_invalidToken() {
        //given
        String token = JwtFactory.builder()
                .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build()
                .createToken(jwtProperties);

        //when
        boolean result = tokenProvider.validToken(token);

        //then
        assertThat(result).isFalse();
    }

    /*
    Given : jjwt 라이브러리를 사용해 토큰 생성, 만료일은 현재 부터 14일 뒤로 만료되지 않은 토큰
    When : validToken() 메서드를 호출해 유효한 토큰인지 검증한 뒤 결과값 반환
    Then : 반환값이 true 인 것 확인
     */

    @DisplayName("validToken() : 유효한 토큰인 때에 유효성 검증에 성공한다.")
    @Test
    void validToken_validToken() {
        //given
        String token = JwtFactory.withDefaultValues().createToken(jwtProperties);

        //when
        boolean result = tokenProvider.validToken(token);

        //then
        assertThat(result).isTrue();
    }

    /*
    Given : jjwt 라이브러리를 사용해 토큰 생성, 토큰 제목(subject) : 유저 이메일
    When : getAuthentication() 메서드를 호출해 인증 객체 반환
    Then : 반환받은 인증 객체의 유저 이름을 가져와 given절에서 설정한 subject 값(userEmail)과 같은 지 확인
     */

    @DisplayName("getAuthentication() : 토큰 기반으로 인증 정보를 가져올 수 있다.")
    @Test
    void getAuthentication(){
        //given
        String userEmail = "test@gmail.com";
        String token = JwtFactory.builder()
                .subject(userEmail)
                .build()
                .createToken(jwtProperties);

        //when
        Authentication authentication = tokenProvider.getAuthentication(token);

        //then
        assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(userEmail);
    }

    /*
    Given : jjwt 라이브러리를 사용해 토큰 생성, 토큰 claim에 "id" 키를 추가 ("id" : userId")
    When : getUserId() 메서드를 호출해 유저 Id 반환
    Then : 반환받은 유저 Id와 given절에서 설정한 userId이 같은 지 확인
     */

    @DisplayName("getUserId() : 토큰으로 유저 ID를 가져올 수 있다.")
    @Test
    void getUserId(){
        //given
        Long userId = 1L;
        String token = JwtFactory.builder()
                .claims(Map.of("id", userId))
                .build()
                .createToken(jwtProperties);

        //when
        Long userIdByToken = tokenProvider.getUserId(token);

        //then
        assertThat(userIdByToken).isEqualTo(userId);
    }
}
