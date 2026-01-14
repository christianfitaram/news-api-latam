package com.newsapi.newsAPI.models.article;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


import java.util.Date;

@Document(collection = "articles")
public class Article {
    @Id
    private String id;
    private String title;
    private String url;
    private String summary;
    private String text;
    private String source;
    @Field("scraped_at")
    private Date scrapedAt;
    private Item[] topic;
    private Sentiment sentiment;
    private String sample;
    private String[] locations;
    private String[] organizations;
    private String[] persons;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getText() {
        return text;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getScrapedAt() {
        return scrapedAt;
    }

    public void setScrapedAt(Date scrapedAt) {
        this.scrapedAt = scrapedAt;
    }

    public Item[] getTopic() {
        return topic;
    }

    public void setTopic(Item[] topic) {
        this.topic = topic;
    }

    public Sentiment getSentiment() {
        return sentiment;
    }

    public void setSentiment(Sentiment sentiment) {
        this.sentiment = sentiment;
    }

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public String[] getLocations() {
        return locations;
    }

    public void setLocations(String[] locations) {
        this.locations = locations;
    }

    public String[] getOrganizations() {
        return organizations;
    }
    public void setOrganizations(String[] organizations) {
        this.organizations = organizations;
    }

    public String[] getPersons() {
        return persons;
    }

    public void setPersons(String[] persons) {
        this.persons = persons;
    }
}