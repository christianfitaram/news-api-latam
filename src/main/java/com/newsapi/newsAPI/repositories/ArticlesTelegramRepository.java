package com.newsapi.newsAPI.repositories;


import com.newsapi.newsAPI.models.article.ArticleTelegram;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.Date;
import java.util.Optional;


@Repository
public interface ArticlesTelegramRepository extends PagingAndSortingRepository<ArticleTelegram, String> {
    Page<ArticleTelegram> findByScrapedAt(Date scrapedAt, Pageable pageable);
    Page<ArticleTelegram> findByTelegramDateBetween(Date start, Date end, Pageable pageable);
    Page<ArticleTelegram> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<ArticleTelegram> findByTextContainingIgnoreCase(String text, Pageable pageable);
    Page<ArticleTelegram> findBySourceContainingIgnoreCase(String source, Pageable pageable);
    Optional<ArticleTelegram> findById(String id);
}
