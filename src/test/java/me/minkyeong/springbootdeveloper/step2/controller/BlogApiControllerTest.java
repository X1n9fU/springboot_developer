package me.minkyeong.springbootdeveloper.step2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.minkyeong.springbootdeveloper.step2.domain.Article;
import me.minkyeong.springbootdeveloper.step2.domain.User;
import me.minkyeong.springbootdeveloper.step2.dto.request.AddArticleRequest;
import me.minkyeong.springbootdeveloper.step2.dto.request.UpdateArticleRequest;
import me.minkyeong.springbootdeveloper.step2.repository.BlogRepository;
import me.minkyeong.springbootdeveloper.step2.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest //테스트용 애플리케이션 컨텍스트
@AutoConfigureMockMvc // MockMvc 생성 및 자동 구성
class BlogApiControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;
    //자바 객체를 JSON 데이터로 변환하는 직렬화 , JSON 데이터를 자바 객체로 변환하는 역직렬화를 위한 클래스

    @Autowired
    private WebApplicationContext context;

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    UserRepository userRepository;

    User user;

    @BeforeEach
    public void mockMvcSetUp(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
        blogRepository.deleteAll();
    }

    @BeforeEach
    void setSecurityContext() {
        userRepository.deleteAll();
        user = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities()));
    }

    /*
    Given : 블로그 글 추가에 필요한 요청 객체를 만든다.
    When : 블로그 글 추가 API에 요청을 보낸다. 요청 타입 : JSON / given에서 만들어 둔 객체를 요청 본문에 함께 보낸다.
    Then : 요청 코드가 201 Created 인지 확인, Blog를 전체 조회하여 크기가 1인지 확인, 실제로 저장된 데이터와 요청 값 비교
     */

    @DisplayName("addArticle : 블로그에 글 추가에 성공한다.")
    @Test
    public void addArticle() throws Exception {
        //given
        final String url = "/api/articles";
        final String title = "title";
        final String content = "content";
        final AddArticleRequest userRequest = new AddArticleRequest(title, content);

        //객체 JSON으로 직렬화
        final String requestBody = objectMapper.writeValueAsString(userRequest);

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("username");

        //when
        //설정한 내용을 바탕으로 요청 전송
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .principal(principal)
                .content(requestBody));

        //then
        List<Article> articles = blogRepository.findAll();

        result.andExpect(status().isCreated());

        assertThat(articles.size()).isEqualTo(1);
        assertThat(articles.get(0).getTitle()).isEqualTo(title);
        assertThat(articles.get(0).getContent()).isEqualTo(content);
    }

    /*
    Given : 블로그 글을 저장
    When : 목록 조회 API를 호출
    Then : 응답코드가 200 OK? 반환받은 값 중에 0번째 요소의 content 와 title이 저장된 값과 같은지 확인
     */

    @DisplayName("findAllArticles : 블로그 글 목록 조회에 성공한다.")
    @Test
    public void findAllArticles() throws Exception{
        //given
        final String url = "/api/articles";
        Article savedArticle = createDefaultArticle();

        //when
        ResultActions result = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(savedArticle.getContent()))
                .andExpect(jsonPath("$[0].title").value(savedArticle.getTitle()));
    }

    /*
    Given : 블로그 글 저장
    When : 저장한 블로그 글의 id 값으로 API 호출
    Then : 응답코드가 200 Ok, 반환받은 content와 title이 저장된 값과 같은지 확인
     */

    @DisplayName("findArticle : 블로그 글 조회에 성공한다.")
    @Test
    public void findArticle() throws Exception{
        //given
        final String url = "/api/articles/{id}";
        Article savedArticle = createDefaultArticle();


        //when
        final ResultActions result = mockMvc.perform(get(url, savedArticle.getId()));

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(savedArticle.getContent()))
                .andExpect(jsonPath("$.title").value(savedArticle.getTitle()));
    }

    /*
    Given : 블로그 글 저장
    When : 저장한 블로그 글의 id 값으로 삭제 API 호출
    Then : 응답 코드 200 OK, 블로그 글 리스트를 전체 조회해 조회한 배열 크기가 0인지 확인
     */
    @DisplayName("deleteArticle : 블로그 글 삭제에 성공한다.")
    @Test
    public void deleteArticle() throws Exception{
        //given
        final String url = "/api/articles/{id}";
        Article savedArticle = createDefaultArticle();


        //when
        mockMvc.perform(delete(url, savedArticle.getId())).andExpect(status().isOk());

        //then
        List<Article> articles = blogRepository.findAll();

        assertThat(articles.size()).isZero();
        assertThat(articles).isEmpty();
        //같은 동작
    }

    /*
    Given : 블로그 글 저장
    When : UPDATE API로 수정 요청을 보냄, 요청 타입 : JSON, given에서 만들어둔 객체를 요청 본문으로 함께 보냄
    Then : 200 OK , id로 조회한 후에 값이 수정되었는 지 확인
     */
    @DisplayName("updateArticle : 블로그 글 수정에 성공한다.")
    @Test
    public void updateArticle() throws Exception{
        //given
        final String url = "/api/articles/{id}";
        Article savedArticle = createDefaultArticle();


        final String newTitle = "newTitle";
        final String newContent = "newContent";

        UpdateArticleRequest request = new UpdateArticleRequest(newTitle, newContent);

        //when
        ResultActions result = mockMvc.perform(put(url, savedArticle.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        result.andExpect(status().isOk());
        Article article = blogRepository.findById(savedArticle.getId()).get();

        assertThat(article.getTitle()).isEqualTo(newTitle);
        assertThat(article.getContent()).isEqualTo(newContent);
    }

    private Article createDefaultArticle(){
        return blogRepository.save(Article.builder()
                .title("title")
                .content("content")
                .author(user.getUsername())
                .build());
    }


}