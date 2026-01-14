package com.newsapi.newsAPI.controllers;

import com.newsapi.newsAPI.models.ApiResponse;
import com.newsapi.newsAPI.models.PaginatedResponse;
import com.newsapi.newsAPI.models.article.Article;
import com.newsapi.newsAPI.models.article.EntityCount;
import com.newsapi.newsAPI.services.articles.ArticlesService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/news")
@CrossOrigin(origins = "*")
public class ArticlesController {
    private final ArticlesService articlesService;

    public ArticlesController(ArticlesService articlesService) {
        this.articlesService = articlesService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<Article>>> getPaginatedArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<Article> articles = articlesService.getArticles(pageable);
        return ResponseEntity.ok(ApiResponse.success("Paginated news articles", articles));
    }

    @GetMapping("/by-topic")
    public ResponseEntity<ApiResponse<PaginatedResponse<Article>>> getPaginatedArticlesByTopic(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam String value) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<Article> articles = articlesService.getArticlesByTopic(pageable, value);
        return ResponseEntity.ok(ApiResponse.success("Paginated news articles by topic", articles));
    }

    @GetMapping("/by-date")
    public ResponseEntity<ApiResponse<PaginatedResponse<Article>>> getPaginatedArticlesByDate(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam String value) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<Article> articles = articlesService.getArticlesByDate(pageable, value);
        return ResponseEntity.ok(ApiResponse.success("Paginated news articles by date", articles));
    }

    @GetMapping("/by-title")
    public ResponseEntity<ApiResponse<PaginatedResponse<Article>>> getPaginatedArticlesByTitle(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam String value) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<Article> articles = articlesService.getArticlesByTitle(pageable, value);
        return ResponseEntity.ok(ApiResponse.success("Paginated news articles by title", articles));
    }

    @GetMapping("/top-entities")
    public ResponseEntity<ApiResponse<List<EntityCount>>> getTopEntities(
            @RequestParam(name = "start_date") String startDate,
            @RequestParam(name = "end_date") String endDate,
            @RequestParam String entity,
            @RequestParam int limit) {
        List<EntityCount> topEntities = articlesService.getTopEntities(startDate, endDate, entity, limit);
        return ResponseEntity.ok(ApiResponse.success("Top entities by frequency", topEntities));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Article>> getArticleById(@PathVariable String id) {
        Article article = articlesService.getArticleById(id);
        return ResponseEntity.ok(ApiResponse.success("Article details", article));
    }

    private PageRequest buildPageRequest(int page, int size, String sort) {
        Sort.Direction direction = Sort.Direction.fromOptionalString(sort).orElse(Sort.Direction.DESC);
        return PageRequest.of(page, size, Sort.by(direction, "scrapedAt"));
    }

}
