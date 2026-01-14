package com.newsapi.newsAPI.services.pipeline;

import com.newsapi.newsAPI.exceptions.ResourceNotFoundException;
import com.newsapi.newsAPI.models.PaginatedResponse;
import com.newsapi.newsAPI.models.pipeline.PipelineLog;
import com.newsapi.newsAPI.repositories.PipelineLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;

@Service
public class PipelineLogServiceImpl implements PipelineLogService {
    private final PipelineLogRepository pipelineLogRepository;

    public PipelineLogServiceImpl(PipelineLogRepository pipelineLogRepository) {
        this.pipelineLogRepository = pipelineLogRepository;
    }

    @Override
    public PaginatedResponse<PipelineLog> getLogs(Pageable pageable) {
        Page<PipelineLog> page = pipelineLogRepository.findAll(pageable);
        return toPaginatedResponse(page, pageable);
    }

    @Override
    public PaginatedResponse<PipelineLog> getLogsByActor(Pageable pageable, String actor) {
        Page<PipelineLog> page = pipelineLogRepository.findByActor(actor, pageable);
        return toPaginatedResponse(page, pageable);
    }

    @Override
    public PaginatedResponse<PipelineLog> getLogsByAction(Pageable pageable, String action) {
        Page<PipelineLog> page = pipelineLogRepository.findByAction(action, pageable);
        return toPaginatedResponse(page, pageable);
    }

    @Override
    public PaginatedResponse<PipelineLog> getLogsByStatus(Pageable pageable, String status) {
        Page<PipelineLog> page = pipelineLogRepository.findByStatus(status, pageable);
        return toPaginatedResponse(page, pageable);
    }

    @Override
    public PaginatedResponse<PipelineLog> getLogsByArticleId(Pageable pageable, String articleId) {
        Page<PipelineLog> page = pipelineLogRepository.findByArticleId(articleId, pageable);
        return toPaginatedResponse(page, pageable);
    }

    @Override
    public PaginatedResponse<PipelineLog> getLogsByEventId(Pageable pageable, String eventId) {
        Page<PipelineLog> page = pipelineLogRepository.findByEventId(eventId, pageable);
        return toPaginatedResponse(page, pageable);
    }

    @Override
    public PaginatedResponse<PipelineLog> getLogsByDate(Pageable pageable, String date) {
        LocalDate localDate = LocalDate.parse(date); // expects "yyyy-MM-dd"
        Date start = java.sql.Date.valueOf(localDate);
        Date end = java.sql.Date.valueOf(localDate.plusDays(1));
        Page<PipelineLog> page = pipelineLogRepository.findByTsBetween(start, end, pageable);
        return toPaginatedResponse(page, pageable);
    }

    @Override
    public PipelineLog getLogById(String id) {
        return pipelineLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pipeline log not found with id: " + id));
    }

    private PaginatedResponse<PipelineLog> toPaginatedResponse(Page<PipelineLog> page, Pageable pageable) {
        return new PaginatedResponse<>(
                page.getContent(),
                (int) page.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize()
        );
    }
}
