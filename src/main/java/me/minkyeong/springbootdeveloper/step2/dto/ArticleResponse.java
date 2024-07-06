package me.minkyeong.springbootdeveloper.step2.dto;

import lombok.Getter;
import me.minkyeong.springbootdeveloper.step2.domain.Article;

@Getter
public class ArticleResponse {
    private final String title;
    private final String content;

    public ArticleResponse(Article article){
        this.title = article.getTitle();
        this.content = article.getContent();
    }
}
