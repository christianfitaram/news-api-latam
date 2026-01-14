package com.newsapi.newsAPI.controllers;

import com.newsapi.newsAPI.models.ApiResponse;
import com.newsapi.newsAPI.models.PaginatedResponse;
import com.newsapi.newsAPI.models.pipeline.PipelineLog;
import com.newsapi.newsAPI.services.pipeline.PipelineLogService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/pipeline-logs")
@CrossOrigin(origins = "*")
public class PipelineLogController {
    private final PipelineLogService pipelineLogService;

    public PipelineLogController(PipelineLogService pipelineLogService) {
        this.pipelineLogService = pipelineLogService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<PipelineLog>>> getPaginatedLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<PipelineLog> logs = pipelineLogService.getLogs(pageable);
        return ResponseEntity.ok(ApiResponse.success("Paginated pipeline logs", logs));
    }

    @GetMapping("/by-actor")
    public ResponseEntity<ApiResponse<PaginatedResponse<PipelineLog>>> getLogsByActor(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam String value) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<PipelineLog> logs = pipelineLogService.getLogsByActor(pageable, value);
        return ResponseEntity.ok(ApiResponse.success("Paginated pipeline logs by actor", logs));
    }

    @GetMapping("/by-action")
    public ResponseEntity<ApiResponse<PaginatedResponse<PipelineLog>>> getLogsByAction(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam String value) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<PipelineLog> logs = pipelineLogService.getLogsByAction(pageable, value);
        return ResponseEntity.ok(ApiResponse.success("Paginated pipeline logs by action", logs));
    }

    @GetMapping("/by-status")
    public ResponseEntity<ApiResponse<PaginatedResponse<PipelineLog>>> getLogsByStatus(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam String value) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<PipelineLog> logs = pipelineLogService.getLogsByStatus(pageable, value);
        return ResponseEntity.ok(ApiResponse.success("Paginated pipeline logs by status", logs));
    }

    @GetMapping("/by-article")
    public ResponseEntity<ApiResponse<PaginatedResponse<PipelineLog>>> getLogsByArticle(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam String value) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<PipelineLog> logs = pipelineLogService.getLogsByArticleId(pageable, value);
        return ResponseEntity.ok(ApiResponse.success("Paginated pipeline logs by article", logs));
    }

    @GetMapping("/by-event")
    public ResponseEntity<ApiResponse<PaginatedResponse<PipelineLog>>> getLogsByEvent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam String value) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<PipelineLog> logs = pipelineLogService.getLogsByEventId(pageable, value);
        return ResponseEntity.ok(ApiResponse.success("Paginated pipeline logs by event", logs));
    }

    @GetMapping("/by-date")
    public ResponseEntity<ApiResponse<PaginatedResponse<PipelineLog>>> getLogsByDate(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam String value) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<PipelineLog> logs = pipelineLogService.getLogsByDate(pageable, value);
        return ResponseEntity.ok(ApiResponse.success("Paginated pipeline logs by date", logs));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PipelineLog>> getLogById(@PathVariable String id) {
        PipelineLog log = pipelineLogService.getLogById(id);
        return ResponseEntity.ok(ApiResponse.success("Pipeline log details", log));
    }

    private PageRequest buildPageRequest(int page, int size, String sort) {
        Sort.Direction direction = Sort.Direction.fromOptionalString(sort).orElse(Sort.Direction.DESC);
        return PageRequest.of(page, size, Sort.by(direction, "ts"));
    }
}
