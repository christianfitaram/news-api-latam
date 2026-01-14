package com.newsapi.newsAPI.repositories;

import com.newsapi.newsAPI.models.RequestLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;


import java.util.List;

public interface RequestLogRepository  extends MongoRepository<RequestLog, String> {

    List<RequestLog> findByApiKey(String apiKey);
    Page<RequestLog> findByApiKeyOrderByTimestampDesc(String apiKey, Pageable pageable);

    long countByApiKeyAndSuccess(String apiKey, boolean success);

    @Aggregation(pipeline = {
            "{ '$match': { 'apiKey': ?0 } }",
            "{ '$group': { '_id': null, 'avgDuration': { '$avg': '$durationMillis' } } }"
    })
    Double findAverageDurationByApiKey(String apiKey);
}
