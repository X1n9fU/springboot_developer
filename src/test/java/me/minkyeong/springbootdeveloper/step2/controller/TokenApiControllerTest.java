package me.minkyeong.springbootdeveloper.step2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.minkyeong.springbootdeveloper.step2.config.jwt.JwtFactory;
import me.minkyeong.springbootdeveloper.step2.config.jwt.JwtProperties;
import me.minkyeong.springbootdeveloper.step2.domain.RefreshToken;
import me.minkyeong.springbootdeveloper.step2.domain.User;
import me.minkyeong.springbootdeveloper.step2.dto.request.CreateAccessTokenRequest;
import me.minkyeong.springbootdeveloper.step2.repository.RefreshTokenRepository;
import me.minkyeong.springbootdeveloper.step2.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TokenApiControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    JwtProperties jwtProperties;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    public void mockMvcSetUp(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
        userRepository.deleteAll();
    }

    /*
    Given : 테스트 유저 생성, jjwt 라이브러리를 사용하여 리프레시 토큰을 만들어 데이터베이스에 저장
        토큰 생성 API의 요청 본문에 리프레시 토큰을 포함하여 요청 객체를 생성
    When : 토큰 추가 API에 요청을 보냄, 요청 타입 : JSON, given 절에서 미리 만들어둔 객체를 요청 본문으로 함께 보냄
    Then : 응답 코드가 201 Created, 엑세스 토큰이 비어있지 않은 지 확인
     */

    @DisplayName("createNewAccessToken : 새로운 액세스 토큰을 발급한다.")
    @Test
    public void createNewAccessToken() throws Exception {
        //given
        final String url = "/api/token";

        User testUser = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        String refreshToken = JwtFactory.builder()
                .claims(Map.of("id", testUser.getId()))
                .build()
                .createToken(jwtProperties);

        refreshTokenRepository.save(new RefreshToken(testUser.getId(), refreshToken));

        CreateAccessTokenRequest request = new CreateAccessTokenRequest();
        request.setRefreshToken(refreshToken);
        final String requestBody = objectMapper.writeValueAsString(request);

        //when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        //then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }
}