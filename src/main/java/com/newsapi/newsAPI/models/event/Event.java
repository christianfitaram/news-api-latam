package com.newsapi.newsAPI.models.event;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@Document(collection = "events")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event {
    @Id
    private String id;

    @Field("label")
    private String title;

    @Field("first_seen_at")
    @JsonProperty("timestamp")
    @JsonAlias("firstSeenAt")
    private Date firstSeenAt;

    private Double lat;
    
    @Field("lon")
    private Double lng;

    private String type;

    @Field("category")
    private String category;
    
    @Field("top_article_url")
    private String url; 

    @Field("summary")
    private String description;

    @Field("contextual_briefing")
    private String contextualBriefing;

    @Field("language")
    private String language;
    
    @Field("location_name")
    private String location;

    private String status;


    private String country;
    private String region;
    private String source;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean verified;
    @Field("created_by")
    private String createdBy;

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

    public Date getFirstSeenAt() {
        return firstSeenAt;
    }
    public void setFirstSeenAt(Date firstSeenAt) {
        this.firstSeenAt = firstSeenAt;
    }

    public Double getLat() {
        return lat;
    }
    public void setLat(Double lat) {
        this.lat = lat;
    }
    
    public Double getLng() {
        return lng;
    }
    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }
    public void setRegion(String region) {
        this.region = region;   
    }

    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public Boolean getVerified() {
        return verified;
    }
    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getContextualBriefing() {
        return contextualBriefing;
    }

    public void setContextualBriefing(String contextualBriefing) {
        this.contextualBriefing = contextualBriefing;
    }
}
