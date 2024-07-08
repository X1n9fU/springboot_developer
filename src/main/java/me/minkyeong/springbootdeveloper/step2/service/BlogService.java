package me.minkyeong.springbootdeveloper.step2.service;

import lombok.RequiredArgsConstructor;
import me.minkyeong.springbootdeveloper.step2.domain.Article;
import me.minkyeong.springbootdeveloper.step2.dto.request.AddArticleRequest;
import me.minkyeong.springbootdeveloper.step2.dto.request.UpdateArticleRequest;
import me.minkyeong.springbootdeveloper.step2.repository.BlogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor //Bean을 생성자로 생성
// final이 붙거나 @NotNull 이 붙은 필드의 생성자 추가
@Service //해당 클래스를 Bean으로 서블릿 컨테이너에 등록
public class BlogService {
    private final BlogRepository blogRepository;

    //블로그 글 추가 메서드
    public Article save (AddArticleRequest request){
        return blogRepository.save(request.toEntity());
    }

    public List<Article> findAll(){
        return blogRepository.findAll();
    }

    public Article findById(long id){
        return blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found:" + id));
    }

    public void delete(long id){
        blogRepository.deleteById(id);
    }

    @Transactional //트랜잭션 메서드
    public Article update (long id, UpdateArticleRequest request){
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        article.update(request.getTitle(), request.getContent());

        return article;
    }
}
