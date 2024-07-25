package me.minkyeong.springbootdeveloper.step2.service;

import lombok.RequiredArgsConstructor;
import me.minkyeong.springbootdeveloper.step2.config.jwt.TokenProvider;
import me.minkyeong.springbootdeveloper.step2.domain.RefreshToken;
import me.minkyeong.springbootdeveloper.step2.repository.RefreshTokenRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;

    public RefreshToken findByRefreshToken(String refreshToken){
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected token"));
    }

    @Transactional
    public void delete() {
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        Long userId = tokenProvider.getUserId(token);

        refreshTokenRepository.deleteByUserId(userId);
    }

}
