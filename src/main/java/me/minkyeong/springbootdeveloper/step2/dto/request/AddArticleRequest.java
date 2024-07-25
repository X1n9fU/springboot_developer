package me.minkyeong.springbootdeveloper.step2.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.minkyeong.springbootdeveloper.step2.domain.Article;

@NoArgsConstructor //기본 생성자
@AllArgsConstructor //모든 필드 값을 파라미터로 받는 생성자 추가
@Getter
public class AddArticleRequest {

    private String title;
    private String content;

    //생성자를 이용해 객체 생성
    public Article toEntity(String author){
        return Article.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }
    //빌더 패턴을 사용해 DTO를 엔티티로 만들어주는 메서드
}
