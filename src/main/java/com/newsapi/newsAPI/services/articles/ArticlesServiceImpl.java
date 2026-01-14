package com.newsapi.newsAPI.services.articles;

import com.newsapi.newsAPI.exceptions.ResourceNotFoundException;
import com.newsapi.newsAPI.models.PaginatedResponse;
import com.newsapi.newsAPI.models.article.Article;
import com.newsapi.newsAPI.models.article.EntityCount;
import com.newsapi.newsAPI.repositories.ArticlesRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class ArticlesServiceImpl implements ArticlesService {
    private final ArticlesRepository articlesRepository;
    private final MongoTemplate mongoTemplate;

    public ArticlesServiceImpl(ArticlesRepository articlesRepository, MongoTemplate mongoTemplate) {
        this.articlesRepository = articlesRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public PaginatedResponse<Article> getArticles(Pageable pageable) {
        Page<Article> articlePage = articlesRepository.findAll(pageable);
        return new PaginatedResponse<>(
                articlePage.getContent(),
                (int) articlePage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize()
        );
    }

    @Override
    public PaginatedResponse<Article> getArticlesByTopic(Pageable pageable, String topic) {
        Page<Article> articlePage = articlesRepository.findByTopic(topic, pageable);
        return new PaginatedResponse<>(
                articlePage.getContent(),
                (int) articlePage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize()
        );
    }

    @Override
    public PaginatedResponse<Article> getArticlesByDate(Pageable pageable, String date) {
        // Parse the string to LocalDate, then to Date
        LocalDate localDate = LocalDate.parse(date); // expects "yyyy-MM-dd"
        Date start = java.sql.Date.valueOf(localDate);
        Date end = java.sql.Date.valueOf(localDate.plusDays(1));
        // Query for articles where scrapedAt >= start and < end
        Page<Article> articlePage = articlesRepository.findByScrapedAtBetween(start, end, pageable);
        return new PaginatedResponse<>(
                articlePage.getContent(),
                (int) articlePage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize()
        );
    }

    @Override
    public PaginatedResponse<Article> getArticlesByTitle(Pageable pageable, String title) {
        Page<Article> articlePage = articlesRepository.findByTitleContainingIgnoreCase(title, pageable);
        return new PaginatedResponse<>(
                articlePage.getContent(),
                (int) articlePage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize()
        );
    }

    @Override
    public List<EntityCount> getTopEntities(String startDate, String endDate, String entity, int limit) {
        String field = resolveEntityField(entity);
        if (limit < 1 || limit > 100) {
            throw new IllegalArgumentException("limit must be between 1 and 100.");
        }

        LocalDate startLocal = parseRequiredDate(startDate, "start_date");
        LocalDate endLocal = parseRequiredDate(endDate, "end_date");
        if (endLocal.isBefore(startLocal)) {
            throw new IllegalArgumentException("end_date must be on or after start_date.");
        }

        Date start = java.sql.Date.valueOf(startLocal);
        Date end = java.sql.Date.valueOf(endLocal.plusDays(1));

        MatchOperation match = Aggregation.match(new Criteria().andOperator(
                Criteria.where("scraped_at").gte(start).lt(end),
                Criteria.where(field).ne(null)
        ));
        UnwindOperation unwind = Aggregation.unwind(field);
        MatchOperation matchNonEmpty = Aggregation.match(Criteria.where(field).ne(null).ne(""));
        GroupOperation group = Aggregation.group(field).count().as("count");
        ProjectionOperation project = Aggregation.project("count").and("_id").as("entity");
        SortOperation sort = Aggregation.sort(
                Sort.by(Sort.Order.desc("count"), Sort.Order.asc("entity"))
        );
        LimitOperation limitOp = Aggregation.limit(limit);

        Aggregation aggregation = Aggregation.newAggregation(
                match,
                unwind,
                matchNonEmpty,
                group,
                project,
                sort,
                limitOp
        );

        AggregationResults<EntityCount> results =
                mongoTemplate.aggregate(aggregation, "articles", EntityCount.class);
        return results.getMappedResults();
    }

    @Override
    public Article getArticleById(String id) {
        return articlesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));
    }

    private String resolveEntityField(String entity) {
        if (entity == null || entity.isBlank()) {
            throw new IllegalArgumentException("entity must be one of: persons, locations, organizations.");
        }
        String normalized = entity.trim().toLowerCase();
        switch (normalized) {
            case "persons":
            case "locations":
            case "organizations":
                return normalized;
            default:
                throw new IllegalArgumentException("entity must be one of: persons, locations, organizations.");
        }
    }

    private LocalDate parseRequiredDate(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return LocalDate.parse(value);
    }
}
