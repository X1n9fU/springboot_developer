package me.minkyeong.springbootdeveloper.step2.repository;

import me.minkyeong.springbootdeveloper.step2.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<Article, Long> {
}
