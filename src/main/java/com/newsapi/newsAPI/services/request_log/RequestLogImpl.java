
package com.newsapi.newsAPI.services.request_log;

import com.newsapi.newsAPI.models.RequestLog;
import com.newsapi.newsAPI.models.RequestLogAnalyticsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.*;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RequestLogImpl implements RequestLogService {

    @Autowired
    private final MongoTemplate mongoTemplate;

    @Autowired
    public RequestLogImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public RequestLogAnalyticsResponse<RequestLog> getLogsByApiKey(String apiKey, int page, Optional<String> method) {
        return getLogsByApiKey(apiKey, page, Optional.empty(), Optional.empty(), method);
    }

    @Override
    public RequestLogAnalyticsResponse<RequestLog> getLogsByApiKey(
            String apiKey,
            int page,
            Optional<String> dateStr,
            Optional<Boolean> successFilter,
            Optional<String> methodFilter) {

        int pageSize = 10;
        PageRequest pageable = PageRequest.of(page, pageSize);

        // === BASE FILTER ===
        List<Criteria> baseCriteriaList = new ArrayList<>();
        baseCriteriaList.add(Criteria.where("apiKey").is(apiKey));

        // Add date filter
        dateStr.ifPresent(date -> {
            LocalDate localDate = LocalDate.parse(date);
            Instant startOfDay = localDate.atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant endOfDay = localDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
            baseCriteriaList.add(Criteria.where("timestamp").gte(Date.from(startOfDay)).lt(Date.from(endOfDay)));
        });

        Optional<String> normalizedMethod = methodFilter
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .map(String::toUpperCase);
        normalizedMethod.ifPresent(method -> baseCriteriaList.add(Criteria.where("method").is(method)));

        Criteria baseCriteria = new Criteria().andOperator(baseCriteriaList.toArray(new Criteria[0]));

        // === QUERY FILTER (adds optional success filter) ===
        Criteria queryCriteria = baseCriteria;
        if (successFilter.isPresent()) {
            queryCriteria = new Criteria().andOperator(baseCriteria,
                    Criteria.where("success").is(successFilter.get()));
        }

        // === Fetch paginated logs ===
        Query query = new Query(queryCriteria)
                .with(Sort.by(Sort.Direction.DESC, "timestamp"))
                .with(pageable);
        List<RequestLog> logs = mongoTemplate.find(query, RequestLog.class);
        long totalFiltered = mongoTemplate.count(new Query(queryCriteria), RequestLog.class);

        // === Aggregation for counts ===
        MatchOperation match = Aggregation.match(baseCriteria);
        GroupOperation group = Aggregation.group()
                .sum(ConditionalOperators.when(Criteria.where("success").is(true)).then(1).otherwise(0)).as("successCount")
                .sum(ConditionalOperators.when(Criteria.where("success").is(false)).then(1).otherwise(0)).as("failureCount")
                .avg("durationMillis").as("avgDuration");

        Aggregation aggregation = Aggregation.newAggregation(match, group);
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "request_logs", Document.class);

        int successCount = 0;
        int failureCount = 0;
        double avgDuration = 0.0;

        Document doc = results.getUniqueMappedResult();
        if (doc != null) {
            int rawSuccess = doc.getInteger("successCount", 0);
            int rawFailure = doc.getInteger("failureCount", 0);
            avgDuration = doc.getDouble("avgDuration") != null ? doc.getDouble("avgDuration") : 0.0;

            if (successFilter.isPresent()) {
                if (successFilter.get()) {
                    successCount = rawSuccess;
                    failureCount = 0;
                } else {
                    successCount = 0;
                    failureCount = rawFailure;
                }
            } else {
                successCount = rawSuccess;
                failureCount = rawFailure;
            }
        }

        return new RequestLogAnalyticsResponse<>(
                logs,
                (int) totalFiltered,
                page,
                pageSize,
                successCount,
                failureCount,
                avgDuration
        );
    }


}
