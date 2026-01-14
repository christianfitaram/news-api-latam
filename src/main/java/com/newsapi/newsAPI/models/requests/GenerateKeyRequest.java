package com.newsapi.newsAPI.models.requests;

import java.time.Instant;

public class GenerateKeyRequest {
    private String user;
    private String adminKey;
    private Long creditsMicros;
    private Instant createdAt;
    private Instant lastChargedAt;
    private String plan;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getAdminKey() {
        return adminKey;
    }

    public void setAdminKey(String adminKey) {
        this.adminKey = adminKey;
    }

    public Long getCreditsMicros() {
        return creditsMicros;
    }

    public void setCreditsMicros(Long creditsMicros) {
        this.creditsMicros = creditsMicros; 
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getLastChargedAt() {
        return lastChargedAt;
    }

    public void setLastChargedAt(Instant lastChargedAt) {
        this.lastChargedAt = lastChargedAt;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

}
