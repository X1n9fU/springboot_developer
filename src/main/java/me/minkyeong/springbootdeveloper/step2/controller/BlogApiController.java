package me.minkyeong.springbootdeveloper.step2.controller;

import lombok.RequiredArgsConstructor;
import me.minkyeong.springbootdeveloper.step2.domain.Article;
import me.minkyeong.springbootdeveloper.step2.dto.request.AddArticleRequest;
import me.minkyeong.springbootdeveloper.step2.dto.response.ArticleResponse;
import me.minkyeong.springbootdeveloper.step2.dto.request.UpdateArticleRequest;
import me.minkyeong.springbootdeveloper.step2.service.BlogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController //HTTP 응답으로 객체 데이터를 JSON 형식으로 반환
public class BlogApiController {
    private final BlogService blogService;

    @PostMapping("/api/articles") //HTTP 메서드가 POST 일 때 전달받은 URL과 동일하면 메서드로 매핑
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request, Principal principal){ //@ResponseBody로 요청 본문 값 매핑
        Article savedArticle = blogService.save(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED) //응답코드로 201, Created 응답
                .body(savedArticle);
    }

    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles(){
        List<ArticleResponse> articles = blogService.findAll()
                .stream()
                .map(ArticleResponse::new)
                .toList();

        return ResponseEntity.ok()
                .body(articles);
    }

    @GetMapping("/api/articles/{id}")
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id){ //@PathvVariable URL에서 값을 가져오는 애너테이션
        Article article = blogService.findById(id);

        return ResponseEntity.ok()
                .body(new ArticleResponse(article));
    }

    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable long id){
        blogService.delete(id);

        return ResponseEntity.ok()
                .build();
    }

    @PutMapping("/api/articles/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable long id, @RequestBody UpdateArticleRequest request){
        Article updateArticle = blogService.update(id, request);

        return ResponseEntity.ok()
                .body(updateArticle);
    }
}
