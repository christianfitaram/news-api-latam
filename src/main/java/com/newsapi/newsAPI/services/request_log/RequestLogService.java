package com.newsapi.newsAPI.services.request_log;

import com.newsapi.newsAPI.models.RequestLog;
import com.newsapi.newsAPI.models.RequestLogAnalyticsResponse;

import java.util.Optional;

public interface RequestLogService {
    RequestLogAnalyticsResponse<RequestLog> getLogsByApiKey(String apiKey, int page, Optional<String> method);
    RequestLogAnalyticsResponse<RequestLog> getLogsByApiKey(String apiKey,
                                                            int page,
                                                            Optional<String> date,
                                                            Optional<Boolean> successFilter,
                                                            Optional<String> method);

}
