package com.newsapi.newsAPI.models.article;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "articles_telegram")
public class ArticleTelegram {
    @Id
    private String id;

    private String source;
    @Field("external_id")
    private int externalId;
    @CreatedDate
    @Field("created_at")
    private Date createdAt;
    private String language;
    @Field("scraped_at")
    private Date scrapedAt;
    @Field("telegram_channel")
    private String telegramChannel;
    @Field("telegram_date")
    private Date telegramDate;
    @Field("telegram_url")
    private String telegramUrl;
    private String text;
    @Field("text_en")
    private String textEn;
    @Field("text_original")
    private String textOriginal;
    private String title;
    @LastModifiedDate
    @Field("updated_at")
    private Date updatedAt;
    private String url;
    private Sentiment sentiment;
    private String topic;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getExternalId() {
        return externalId;
    }

    public void setExternalId(int externalId) {
        this.externalId = externalId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Date getScrapedAt() {
        return scrapedAt;
    }

    public void setScrapedAt(Date scrapedAt) {
        this.scrapedAt = scrapedAt;
    }

    public String getTelegramChannel() {
        return telegramChannel;
    }

    public void setTelegramChannel(String telegramChannel) {
        this.telegramChannel = telegramChannel;
    }

    public Date getTelegramDate() {
        return telegramDate;
    }

    public void setTelegramDate(Date telegramDate) {
        this.telegramDate = telegramDate;
    }

    public String getTelegramUrl() {
        return telegramUrl;
    }

    public void setTelegramUrl(String telegramUrl) {
        this.telegramUrl = telegramUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextEn() {
        return textEn;
    }

    public void setTextEn(String textEn) {
        this.textEn = textEn;
    }

    public String getTextOriginal() {
        return textOriginal;
    }

    public void setTextOriginal(String textOriginal) {
        this.textOriginal = textOriginal;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Sentiment getSentiment() {
        return sentiment;
    }

    public void setSentiment(Sentiment sentiment) {
        this.sentiment = sentiment;
    }

    public String getTopic() {
        return topic;
    }
    
    public void setTopic(String topic) {
        this.topic = topic;
    }
}
