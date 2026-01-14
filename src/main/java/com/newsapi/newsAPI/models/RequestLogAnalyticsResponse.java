package com.newsapi.newsAPI.models;

import java.util.List;

public class RequestLogAnalyticsResponse<T> extends PaginatedResponse<T> {
    private long successCount;
    private long failureCount;
    private double averageDurationMillis;

    public RequestLogAnalyticsResponse(List<T> samples, int totalItems, int currentPage, int pageSize,
                                       long successCount, long failureCount, double averageDurationMillis) {
        super(samples, totalItems, currentPage, pageSize);
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.averageDurationMillis = averageDurationMillis;
    }

    // Getters and setters
    public long getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(long successCount) {
        this.successCount = successCount;
    }

    public long getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(long failureCount) {
        this.failureCount = failureCount;
    }

    public double getAverageDurationMillis() {
        return averageDurationMillis;
    }

    public void setAverageDurationMillis(double averageDurationMillis) {
        this.averageDurationMillis = averageDurationMillis;
    }
}
