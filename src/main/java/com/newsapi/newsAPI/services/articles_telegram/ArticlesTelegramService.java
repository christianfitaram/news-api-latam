package com.newsapi.newsAPI.services.articles_telegram;
import com.newsapi.newsAPI.models.PaginatedResponse;
import com.newsapi.newsAPI.models.article.ArticleTelegram;
import org.springframework.data.domain.Pageable;
public interface ArticlesTelegramService {

    PaginatedResponse<ArticleTelegram> getArticlesTelegram(Pageable pageable);
    PaginatedResponse<ArticleTelegram> getArticlesTelegramByDate(Pageable pageable, String date);
    PaginatedResponse<ArticleTelegram> getArticlesTelegramByTitle(Pageable pageable, String title);
    PaginatedResponse<ArticleTelegram> getArticlesTelegramByText(Pageable pageable, String text);
    PaginatedResponse<ArticleTelegram> searchArticlesTelegram(Pageable pageable, String title, String text, String source, String start, String end);
    PaginatedResponse<ArticleTelegram> getArticlesTelegramBySource(Pageable pageable, String source);
    ArticleTelegram getArticleTelegramById(String id);
    
}
