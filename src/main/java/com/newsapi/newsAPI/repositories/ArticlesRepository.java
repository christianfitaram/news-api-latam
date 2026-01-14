package com.newsapi.newsAPI.repositories;


import com.newsapi.newsAPI.models.article.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.Date;
import java.util.Optional;


@Repository
public interface ArticlesRepository extends PagingAndSortingRepository<Article, String> {
    Page<Article> findByTopic(String topic, Pageable pageable);
    Page<Article> findByScrapedAt(Date scrapedAt, Pageable pageable);
    Page<Article> findByScrapedAtBetween(Date start, Date end, Pageable pageable);
    Page<Article> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Optional<Article> findById(String id);
}
