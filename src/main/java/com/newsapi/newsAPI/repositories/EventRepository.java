package com.newsapi.newsAPI.repositories;

import com.newsapi.newsAPI.models.event.Event;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {
    Page<Event> findByCategory(String category, Pageable pageable);
    Page<Event> findByFirstSeenAtBetween(Date start, Date end, Pageable pageable);
    Page<Event> findByStatus(String status, Pageable pageable);
    Page<Event> findByCreatedBy(String createdBy, Pageable pageable);
    Page<Event> findByVerified(Boolean verified, Pageable pageable);
    Page<Event> findByCreatedByAndVerified(String createdBy, Boolean verified, Pageable pageable);
    Page<Event> findByCreatedByAndVerifiedIsNull(String createdBy, Pageable pageable);
    Page<Event> findByCreatedByAndFirstSeenAtBetween(String createdBy, Date start, Date end, Pageable pageable);
    Page<Event> findByVerifiedAndFirstSeenAtBetween(Boolean verified, Date start, Date end, Pageable pageable);
    Page<Event> findByVerifiedIsNull(Pageable pageable);
    Page<Event> findByVerifiedIsNullAndFirstSeenAtBetween(Date start, Date end, Pageable pageable);
    Page<Event> findByCreatedByAndVerifiedIsNullAndFirstSeenAtBetween(
            String createdBy,
            Date start,
            Date end,
            Pageable pageable);
    Page<Event> findByCreatedByAndVerifiedAndFirstSeenAtBetween(
            String createdBy,
            Boolean verified,
            Date start,
            Date end,
            Pageable pageable);

    default Page<Event> findByCreatedBy(String createdBy, Date start, Date end, Pageable pageable) {
        if (start == null || end == null) {
            return findByCreatedBy(createdBy, pageable);
        }
        return findByCreatedByAndFirstSeenAtBetween(createdBy, start, end, pageable);
    }

    default Page<Event> findByVerified(Boolean verified, Date start, Date end, Pageable pageable) {
        if (start == null || end == null) {
            return findByVerified(verified, pageable);
        }
        return findByVerifiedAndFirstSeenAtBetween(verified, start, end, pageable);
    }

    default Page<Event> findByVerifiedIsNull(Date start, Date end, Pageable pageable) {
        if (start == null || end == null) {
            return findByVerifiedIsNull(pageable);
        }
        return findByVerifiedIsNullAndFirstSeenAtBetween(start, end, pageable);
    }

    default Page<Event> findByCreatedByAndVerifiedIsNull(
            String createdBy,
            Date start,
            Date end,
            Pageable pageable) {
        if (start == null || end == null) {
            return findByCreatedByAndVerifiedIsNull(createdBy, pageable);
        }
        return findByCreatedByAndVerifiedIsNullAndFirstSeenAtBetween(createdBy, start, end, pageable);
    }

    default Page<Event> findByCreatedByAndVerified(
            String createdBy,
            Boolean verified,
            Date start,
            Date end,
            Pageable pageable) {
        if (start == null || end == null) {
            return findByCreatedByAndVerified(createdBy, verified, pageable);
        }
        return findByCreatedByAndVerifiedAndFirstSeenAtBetween(createdBy, verified, start, end, pageable);
    }
}
