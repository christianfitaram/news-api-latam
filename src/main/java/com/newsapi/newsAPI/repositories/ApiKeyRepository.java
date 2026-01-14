package com.newsapi.newsAPI.repositories;

import com.newsapi.newsAPI.models.ApiKey;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ApiKeyRepository extends MongoRepository<ApiKey, String> {
    Optional<ApiKey> findByKeyAndActiveTrue(String key);
    Optional<ApiKey> findByKey(String key);
}
