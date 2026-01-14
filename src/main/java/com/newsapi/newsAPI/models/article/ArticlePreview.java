package com.newsapi.newsAPI.models.article;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Field;

public class ArticlePreview {
    @Field("article_id")
    private String articleId;
    private String title;
    private String url;
    private String summary;
    @Field("scraped_at")
    private Date scrapedAt;

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Date getScrapedAt() {
        return scrapedAt;
    }

    public void setScrapedAt(Date scrapedAt) {
        this.scrapedAt = scrapedAt;
    }
}
