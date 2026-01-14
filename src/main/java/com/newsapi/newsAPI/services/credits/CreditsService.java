package com.newsapi.newsAPI.services.credits;

import com.newsapi.newsAPI.models.ApiKey;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;

@Service
public class CreditsService {

    private final MongoTemplate mongoTemplate;

    public CreditsService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public ApiKey debitIfPossible(String apiKey, long costMicros) {
        if (costMicros <= 0) return null;

        Query q = new Query(
                Criteria.where("key").is(apiKey)
                        .and("active").is(true)
                        .and("creditsMicros").gte(costMicros)
        );

        Update u = new Update().inc("creditsMicros", -costMicros);

        // Returns the UPDATED doc atomically
        return mongoTemplate.findAndModify(q, u, options().returnNew(true), ApiKey.class);
    }
}
