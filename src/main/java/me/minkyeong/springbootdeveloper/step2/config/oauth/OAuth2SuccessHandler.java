package me.minkyeong.springbootdeveloper.step2.config.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.minkyeong.springbootdeveloper.step2.config.jwt.TokenProvider;
import me.minkyeong.springbootdeveloper.step2.domain.RefreshToken;
import me.minkyeong.springbootdeveloper.step2.domain.User;
import me.minkyeong.springbootdeveloper.step2.repository.RefreshTokenRepository;
import me.minkyeong.springbootdeveloper.step2.service.UserService;
import me.minkyeong.springbootdeveloper.step2.util.CookieUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

@RequiredArgsConstructor
@Component
//토큰 관련된 작업 추가
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);
    public static final String REDIRECT_PATH = "/articles";

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
                                                                                                                throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        User user = userService.findByEmail((String) oAuth2User.getAttributes().get("email"));

        //리프레시 토큰 생성 -> 저장 -> 쿠키에 저장
        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);
        saveRefreshToken(user.getId(), refreshToken);
        addRefreshTokenToCookie(request, response, refreshToken);

        //액세스 토큰 생성 -> Path에 액세스 토큰 추가
        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
        String targetUrl = getTargetUrl(accessToken);

        //인증 관련 설정값, 쿠키 제거
        clearAuthenticationAttributes(request, response);

        //리다이렉트
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String getTargetUrl(String accessToken) {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                .queryParam("token", accessToken)
                .build()
                .toUriString();
    }

    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }

    private void saveRefreshToken(Long id, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(id)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(id, newRefreshToken));

        refreshTokenRepository.save(refreshToken);
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response){
        super.clearAuthenticationAttributes(request);
        //OAuth 인증을 위해 저장된 정보도 삭제
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }



}
