package com.newsapi.newsAPI.repositories;

import com.newsapi.newsAPI.models.pipeline.PipelineLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface PipelineLogRepository extends PagingAndSortingRepository<PipelineLog, String> {
    Page<PipelineLog> findByActor(String actor, Pageable pageable);
    Page<PipelineLog> findByAction(String action, Pageable pageable);
    Page<PipelineLog> findByStatus(String status, Pageable pageable);
    Page<PipelineLog> findByArticleId(String articleId, Pageable pageable);
    Page<PipelineLog> findByEventId(String eventId, Pageable pageable);
    Page<PipelineLog> findByTsBetween(Date start, Date end, Pageable pageable);
    Optional<PipelineLog> findById(String id);
}
