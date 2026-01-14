package com.newsapi.newsAPI.models;

import java.util.List;

public class PaginatedResponse<T> {
    private List<T> samples;
    private int totalItems;
    private int totalPages;
    private int currentPage;
    private int pageSize;

    public PaginatedResponse(List<T> samples, int totalItems, int currentPage, int pageSize) {
        this.samples = samples;
        this.totalItems = totalItems;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) totalItems / pageSize);
    }

    public PaginatedResponse(){

    }
    // Getters and setters


    public List<T> getSamples() {
        return samples;
    }

    public void setSamples(List<T> samples) {
        this.samples = samples;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
