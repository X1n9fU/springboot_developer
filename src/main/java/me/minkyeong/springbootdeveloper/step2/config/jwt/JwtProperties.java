package me.minkyeong.springbootdeveloper.step2.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties("jwt") //해당 클래스에 프로피티값을 가져와서 사용 가능
public class JwtProperties {
    private String issuer;
    private String secretKey;
}
