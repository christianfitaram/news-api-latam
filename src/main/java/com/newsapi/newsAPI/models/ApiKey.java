package com.newsapi.newsAPI.models;


import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "api_keys")
public class ApiKey {

    @Id
    private String id;

    private String user;
    private String key;
    private boolean active;
    private Long creditsMicros;
    private Instant createdAt;
    private Instant lastChargedAt;
    private String plan;


    public ApiKey() {}

    public ApiKey(String user, String key, boolean active, Long creditsMicros) {
        this.user = user;
        this.key = key;
        this.active = active;
        this.creditsMicros = creditsMicros;
        this.createdAt = Instant.now();
        this.plan = "free";
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public ApiKey(String user, String key, boolean active) {
        this.user = user;
        this.key = key;
        this.active = active;
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
