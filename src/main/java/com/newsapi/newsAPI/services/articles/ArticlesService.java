package com.newsapi.newsAPI.services.articles;

import com.newsapi.newsAPI.models.PaginatedResponse;
import com.newsapi.newsAPI.models.article.Article;
import com.newsapi.newsAPI.models.article.EntityCount;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArticlesService {
    PaginatedResponse<Article> getArticles(Pageable pageable);
    PaginatedResponse<Article> getArticlesByTopic(Pageable pageable, String topic);
    PaginatedResponse<Article> getArticlesByDate(Pageable pageable, String date);
    PaginatedResponse<Article> getArticlesByTitle(Pageable pageable, String title);
    List<EntityCount> getTopEntities(String startDate, String endDate, String entity, int limit);
    Article getArticleById(String id);
}
