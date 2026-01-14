package com.newsapi.newsAPI.models.event;

public class TimeToVerifyStats {
    private double averageSeconds;
    private long totalItems;

    public TimeToVerifyStats() {
    }

    public TimeToVerifyStats(double averageSeconds, long totalItems) {
        this.averageSeconds = averageSeconds;
        this.totalItems = totalItems;
    }

    public double getAverageSeconds() {
        return averageSeconds;
    }

    public void setAverageSeconds(double averageSeconds) {
        this.averageSeconds = averageSeconds;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }
}
