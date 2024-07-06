package me.minkyeong.springbootdeveloper.step1;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import me.minkyeong.springbootdeveloper.step1.Member;
import me.minkyeong.springbootdeveloper.step1.MemberRepository;
import org.junit.jupiter.api.AfterEach;
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

@SpringBootTest //테스트용 애플리케이션 컨텍스트 생성
@AutoConfigureMockMvc //MockMvc 생성 및 자동 구성
class TestControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    public void mockMvcSetUp(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
    }

    @AfterEach
    public void cleanUp(){
        memberRepository.deleteAll();
    }

    @DisplayName("getAllMembers : 아티클 조회에 성공한다")
    @Test
    public void getAllMembers() throws Exception {
        //given
        //멤버 저장
        final String url = "/test";
        Member savedMember = memberRepository.save(new Member(1L, "홍길동"));

        //when
        //멤버 리스트 조회하는 API 호출
        //ResultActions 객체는 반환값을 검증하고 확인하는 andExpect() 메서드 제공
        final ResultActions result = mockMvc.perform(get(url).
                //perform() -> 요청을 전송하는 역할
                        accept(MediaType.APPLICATION_JSON));
                //accept()는 무슨 타입으로 응답을 받을지 결정하는 메서드

        //then
        //응답 코드가 200 OK이고, 반환받은 값 중에 0번째 요소의 id와 name이 저장된 값과 같은지 확인
        result
                //andExpect()는 응답을 검증
                .andExpect(status().isOk()) //응답 코드가 OK(200)인지 확인지
                .andExpect(jsonPath("$[0].id").value(savedMember.getId())) //JSON 응답값을 가져오는 역할
                .andExpect(jsonPath("$[0].name").value(savedMember.getName()));

    }

}