package com.newsapi.newsAPI.models.event;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.newsapi.newsAPI.models.article.ArticlePreview;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Document(collection = "events")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventDTO {
    @Id
    private String id;
    private String label;
    private String summary;
    private String category;
    private String language;
    @Field("location_name")
    private String location;
    @Field("location_confidence")
    private Double locationConfidence;
    @Field("location_granularity")
    private String locationGranularity;
    @Field("location_source")
    private String locationSource;
    private Double lat;
    @Field("lon")
    private Double lng;
    @Field("top_article_url")
    private String url;
    private String country;
    private String region;
    private String source;
    private List<String> tags;

    @Field("centroid_embedding")
    private List<Double> centroidEmbedding;
    @Field("first_seen_at")
    private Date firstSeenAt;
    @Field("last_seen_at")
    private Date lastSeenAt;
    @Field("date_by_human")
    private Date dateByHuman;
    @Field("articles_preview")
    private List<ArticlePreview> articlesPreview;
    @Field("importance_score")
    private Integer importanceScore;
    private String status;
    @Field("source_stats")
    private Map<String, Integer> sourceStats;
    @Field("misc_flags")
    private Map<String, Object> miscFlags;
    private Boolean verified;
    @Field("verified_at")
    private Date verifiedAt;
    @Field("time_to_verify")
    private long timeToVerify;
    @Field("contextual_briefing")
    private String contextualBriefing;
    @Field("created_by")
    private String createdBy;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Double> getCentroidEmbedding() {
        return centroidEmbedding;
    }

    public void setCentroidEmbedding(List<Double> centroidEmbedding) {
        this.centroidEmbedding = centroidEmbedding;
    }

    public Date getFirstSeenAt() {
        return firstSeenAt;
    }

    public void setFirstSeenAt(Date firstSeenAt) {
        this.firstSeenAt = firstSeenAt;
    }

    public Date getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(Date lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    public List<ArticlePreview> getArticlesPreview() {
        return articlesPreview;
    }

    public void setArticlesPreview(List<ArticlePreview> articlesPreview) {
        this.articlesPreview = articlesPreview;
    }

    public Integer getImportanceScore() {
        return importanceScore;
    }

    public void setImportanceScore(Integer importanceScore) {
        this.importanceScore = importanceScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Integer> getSourceStats() {
        return sourceStats;
    }

    public void setSourceStats(Map<String, Integer> sourceStats) {
        this.sourceStats = sourceStats;
    }

    public Map<String, Object> getMiscFlags() {
        return miscFlags;
    }

    public void setMiscFlags(Map<String, Object> miscFlags) {
        this.miscFlags = miscFlags;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLocationConfidence() {
        return locationConfidence;
    }

    public void setLocationConfidence(Double locationConfidence) {
        this.locationConfidence = locationConfidence;
    }

    public String getLocationGranularity() {
        return locationGranularity;
    }

    public void setLocationGranularity(String locationGranularity) {
        this.locationGranularity = locationGranularity;
    }

    public String getLocationSource() {
        return locationSource;
    }

    public void setLocationSource(String locationSource) {
        this.locationSource = locationSource;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public Date getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(Date verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getDateByHuman() {
        return dateByHuman;
    }

    public void setDateByHuman(Date dateByHuman) {
        this.dateByHuman = dateByHuman;
    }

    public long getTimeToVerify() {
        return timeToVerify;
    }

    public void setTimeToVerify(long timeToVerify) {
        this.timeToVerify = timeToVerify;
    }

    public String getContextualBriefing() {
        return contextualBriefing;
    }
    
    public void setContextualBriefing(String contextualBriefing) {
        this.contextualBriefing = contextualBriefing;
    }
}
