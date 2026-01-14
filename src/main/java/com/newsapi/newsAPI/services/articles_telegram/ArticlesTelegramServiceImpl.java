package com.newsapi.newsAPI.services.articles_telegram;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import com.newsapi.newsAPI.exceptions.ResourceNotFoundException;
import com.newsapi.newsAPI.models.PaginatedResponse;
import com.newsapi.newsAPI.models.article.ArticleTelegram;
import com.newsapi.newsAPI.repositories.ArticlesTelegramRepository;
import org.springframework.stereotype.Service;


@Service
public class ArticlesTelegramServiceImpl implements ArticlesTelegramService {
    private final ArticlesTelegramRepository articlesTelegramRepository;
    private final MongoTemplate mongoTemplate;

    public ArticlesTelegramServiceImpl(ArticlesTelegramRepository articlesTelegramRepository,
            MongoTemplate mongoTemplate) {
        this.articlesTelegramRepository = articlesTelegramRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public PaginatedResponse<ArticleTelegram> getArticlesTelegram(Pageable pageable) {
        Page<ArticleTelegram> articleTelegramPage = articlesTelegramRepository.findAll(pageable);
        return new PaginatedResponse<>(
                articleTelegramPage.getContent(),
                (int) articleTelegramPage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize());
    }

    @Override
    public PaginatedResponse<ArticleTelegram> getArticlesTelegramByDate(Pageable pageable, String date) {
        LocalDate localDate = LocalDate.parse(date); // expects "yyyy-MM-dd"
        Date start = java.sql.Date.valueOf(localDate);
        Date end = java.sql.Date.valueOf(localDate.plusDays(1));
        Page<ArticleTelegram> articleTelegramPage = articlesTelegramRepository.findByTelegramDateBetween(start, end,
                pageable);
        return new PaginatedResponse<>(
                articleTelegramPage.getContent(),
                (int) articleTelegramPage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize());
    }

    @Override
    public PaginatedResponse<ArticleTelegram> getArticlesTelegramByTitle(Pageable pageable, String title) {
        Page<ArticleTelegram> articleTelegramPage = articlesTelegramRepository.findByTitleContainingIgnoreCase(title,
                pageable);
        return new PaginatedResponse<>(
                articleTelegramPage.getContent(),
                (int) articleTelegramPage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize());
    }

    @Override
    public PaginatedResponse<ArticleTelegram> getArticlesTelegramByText(Pageable pageable, String text) {
        Page<ArticleTelegram> articleTelegramPage = articlesTelegramRepository.findByTextContainingIgnoreCase(text,
                pageable);
        return new PaginatedResponse<>(
                articleTelegramPage.getContent(),
                (int) articleTelegramPage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize());
    }

    @Override
    public ArticleTelegram getArticleTelegramById(String id) {
        return articlesTelegramRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));
    }

    @Override
    public PaginatedResponse<ArticleTelegram> getArticlesTelegramBySource(Pageable pageable, String source) {
        Page<ArticleTelegram> articleTelegramPage = articlesTelegramRepository.findBySourceContainingIgnoreCase(source,
                pageable);
        return new PaginatedResponse<>(
                articleTelegramPage.getContent(),
                (int) articleTelegramPage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize());
    }

    @Override
    public PaginatedResponse<ArticleTelegram> searchArticlesTelegram(Pageable pageable, String title, String text, String source,
            String start, String end) {
        Query query = new Query();
        List<Criteria> criteria = new ArrayList<>();
        // title filter (case-insensitive "contains")
        if (title != null && !title.isBlank()) {
            Pattern p = Pattern.compile(Pattern.quote(title.trim()), Pattern.CASE_INSENSITIVE);
            criteria.add(Criteria.where("title").regex(p));
        }

        // text filter (case-insensitive "contains")
        if (text != null && !text.isBlank()) {
            Pattern p = Pattern.compile(Pattern.quote(text.trim()), Pattern.CASE_INSENSITIVE);
            criteria.add(Criteria.where("text").regex(p));
        }

        // date filters (compared against firstSeenAt)
        // start_date: inclusive (>= start at 00:00)
        if (start != null && !start.isBlank()) {
            LocalDate sd = LocalDate.parse(start.trim()); // expects yyyy-MM-dd
            Date startDate = java.sql.Date.valueOf(sd);
            criteria.add(Criteria.where("firstSeenAt").gte(startDate));
        }

        // end_date: inclusive by day (we do < (end + 1 day))
        if (end != null && !end.isBlank()) {
            LocalDate ed = LocalDate.parse(end.trim()); // expects yyyy-MM-dd
            Date endExclusive = java.sql.Date.valueOf(ed.plusDays(1));
            criteria.add(Criteria.where("firstSeenAt").lt(endExclusive));
        } 
        
        // source filter (case-insensitive "contains")
        if (source != null && !source.isBlank()) {
            Pattern p = Pattern.compile(Pattern.quote(source.trim()), Pattern.CASE_INSENSITIVE);
            criteria.add(Criteria.where("source").regex(p));
        }

        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }

        long total = mongoTemplate.count(query, ArticleTelegram.class);
        query.with(pageable);
        List<ArticleTelegram> results = mongoTemplate.find(query, ArticleTelegram.class);

        return new PaginatedResponse<>(
                results,
                (int) total,
                pageable.getPageNumber(),
                pageable.getPageSize());
    }

}
