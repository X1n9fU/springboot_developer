package me.minkyeong.springbootdeveloper.step2.service;

import lombok.RequiredArgsConstructor;
import me.minkyeong.springbootdeveloper.step2.domain.RefreshToken;
import me.minkyeong.springbootdeveloper.step2.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken(String refreshToken){
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected token"));
    }
}
