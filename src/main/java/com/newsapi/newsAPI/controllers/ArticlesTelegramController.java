package com.newsapi.newsAPI.controllers;

import org.springframework.web.bind.annotation.*;

import com.newsapi.newsAPI.services.articles_telegram.ArticlesTelegramService;
import com.newsapi.newsAPI.models.ApiResponse;
import com.newsapi.newsAPI.models.PaginatedResponse;
import com.newsapi.newsAPI.models.article.ArticleTelegram;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/v1/telegram")
@CrossOrigin(origins = "*")
public class ArticlesTelegramController {
    private final ArticlesTelegramService articlesTelegramService;

    public ArticlesTelegramController(ArticlesTelegramService articlesTelegramService) {
        this.articlesTelegramService = articlesTelegramService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<ArticleTelegram>>> getPaginatedArticlesTelegram(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<ArticleTelegram> articles = articlesTelegramService.getArticlesTelegram(pageable);
        return ResponseEntity.ok(ApiResponse.success("Paginated telegram news articles", articles));
    }

    @GetMapping("/by-date")
    public ResponseEntity<ApiResponse<PaginatedResponse<ArticleTelegram>>> getPaginatedArticlesTelegramByDate(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam String value) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<ArticleTelegram> articles = articlesTelegramService.getArticlesTelegramByDate(pageable,
                value);
        return ResponseEntity.ok(ApiResponse.success("Paginated telegram news articles by date", articles));
    }

    @GetMapping("/by-title")
    public ResponseEntity<ApiResponse<PaginatedResponse<ArticleTelegram>>> getPaginatedArticlesTelegramByTitle(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam String value) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<ArticleTelegram> articles = articlesTelegramService.getArticlesTelegramByTitle(pageable,
                value);
        return ResponseEntity.ok(ApiResponse.success("Paginated telegram news articles by title", articles));
    }

    @GetMapping("/by-text")
    public ResponseEntity<ApiResponse<PaginatedResponse<ArticleTelegram>>> getPaginatedArticlesTelegramByText(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam String value) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<ArticleTelegram> articles = articlesTelegramService.getArticlesTelegramByText(pageable,
                value);
        return ResponseEntity.ok(ApiResponse.success("Paginated telegram news articles by text", articles));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticleTelegram>> getArticleTelegramById(@PathVariable String id) {
        ArticleTelegram article = articlesTelegramService.getArticleTelegramById(id);
        return ResponseEntity.ok(ApiResponse.success("Telegram news article details", article));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PaginatedResponse<ArticleTelegram>>> searchArticlesTelegram(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String text,
            @RequestParam(required = false) String source,
            @RequestParam(name = "start_date", required = false) String startDate,
            @RequestParam(name = "end_date", required = false) String endDate) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<ArticleTelegram> events = articlesTelegramService.searchArticlesTelegram(pageable, title, 
                text, source, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Search telegram articles", events));
    }

    private PageRequest buildPageRequest(int page, int size, String sort) {
        Sort.Direction direction = Sort.Direction.fromOptionalString(sort).orElse(Sort.Direction.DESC);
        return PageRequest.of(page, size, Sort.by(direction, "telegramDate"));
    }

}
