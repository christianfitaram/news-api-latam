package com.newsapi.newsAPI.services.pipeline;

import com.newsapi.newsAPI.models.PaginatedResponse;
import com.newsapi.newsAPI.models.pipeline.PipelineLog;
import org.springframework.data.domain.Pageable;

public interface PipelineLogService {
    PaginatedResponse<PipelineLog> getLogs(Pageable pageable);
    PaginatedResponse<PipelineLog> getLogsByActor(Pageable pageable, String actor);
    PaginatedResponse<PipelineLog> getLogsByAction(Pageable pageable, String action);
    PaginatedResponse<PipelineLog> getLogsByStatus(Pageable pageable, String status);
    PaginatedResponse<PipelineLog> getLogsByArticleId(Pageable pageable, String articleId);
    PaginatedResponse<PipelineLog> getLogsByEventId(Pageable pageable, String eventId);
    PaginatedResponse<PipelineLog> getLogsByDate(Pageable pageable, String date);
    PipelineLog getLogById(String id);
}
