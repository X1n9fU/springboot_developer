package me.minkyeong.springbootdeveloper.step2.config;

import lombok.RequiredArgsConstructor;
import me.minkyeong.springbootdeveloper.step2.config.jwt.TokenProvider;
import me.minkyeong.springbootdeveloper.step2.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import me.minkyeong.springbootdeveloper.step2.config.oauth.OAuth2SuccessHandler;
import me.minkyeong.springbootdeveloper.step2.config.oauth.OAuth2UserCustomService;
import me.minkyeong.springbootdeveloper.step2.repository.RefreshTokenRepository;
import me.minkyeong.springbootdeveloper.step2.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
@Configuration
public class WebOAuthSecurityConfig {

    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @Bean
    public WebSecurityCustomizer configure(){
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers(
                        new AntPathRequestMatcher("/img/**"),
                        new AntPathRequestMatcher("/css/**"),
                        new AntPathRequestMatcher("/js/**")
                );
    }

    // 토큰 방식으로 인증을 하기 때문에 기존에 사용하던 폼 로그인, 세션 비활성화
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
         return http.csrf(AbstractHttpConfigurer::disable)
                 .httpBasic(AbstractHttpConfigurer::disable)
                 .formLogin(AbstractHttpConfigurer::disable)
                 .logout(AbstractHttpConfigurer::disable)
                 .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                 //헤더를 확인할 커스텀 필터 추가
                 .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)

                 //토큰 재발급 URL은 인증 없이 접근 가능하도록 설정, 나머지 API URL은 인증 필요
                 .authorizeHttpRequests(auth -> auth
                         .requestMatchers(new AntPathRequestMatcher("/api/token")).permitAll()
                         .requestMatchers(new AntPathRequestMatcher("/api/**")).authenticated()
                         .anyRequest().permitAll())

                 .oauth2Login(oauth2 -> oauth2
                         .loginPage("/login")
                         //Authorization 요청과 관련된 상태 저장
                         .authorizationEndpoint(authorizationEndpointConfig ->
                                 authorizationEndpointConfig.authorizationRequestRepository(OAuth2AuthorizationRequestBasedOnCookieRepository()))
                         //oAuth2의 유저 정보 저장 및 업데이트
                         .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(oAuth2UserCustomService))
                         //인증 성공 시 실행한 핸들러
                         .successHandler(oAuth2SuccessHandler())
                 )
                 //api로 시작하는 url인 경우 401 상태 코드를 반환하도록 예외 처리
                 .exceptionHandling(exceptionHandling -> exceptionHandling
                         .defaultAuthenticationEntryPointFor(
                                 new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                 new AntPathRequestMatcher("/api/**")
                         ))
                 .build();
    }


    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler(){
        return new OAuth2SuccessHandler(tokenProvider,
                refreshTokenRepository,
                OAuth2AuthorizationRequestBasedOnCookieRepository(),
                userService
        );
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository OAuth2AuthorizationRequestBasedOnCookieRepository(){
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
