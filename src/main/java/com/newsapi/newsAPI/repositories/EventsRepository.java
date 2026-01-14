package com.newsapi.newsAPI.repositories;

import com.newsapi.newsAPI.models.event.EventDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface EventsRepository extends MongoRepository<EventDTO, String> {
    Page<EventDTO> findByCategory(String category, Pageable pageable);
    Page<EventDTO> findByStatus(String status, Pageable pageable);
    Page<EventDTO> findByFirstSeenAtBetween(Date start, Date end, Pageable pageable);
    Optional<EventDTO> findById(String id);
    Page<EventDTO> findByCreatedBy(String createdBy, Pageable pageable);
    Page<EventDTO> findByVerified(Boolean verified, Pageable pageable);
    Page<EventDTO> findByCreatedByAndVerified(String createdBy, Boolean verified, Pageable pageable);
    Page<EventDTO> findByVerifiedIsNull(Pageable pageable);
    Page<EventDTO> findByCreatedByAndVerifiedIsNull(String createdBy, Pageable pageable);
    Page<EventDTO> findByCreatedByAndFirstSeenAtBetween(String createdBy, Date start, Date end, Pageable pageable);
    Page<EventDTO> findByVerifiedAndFirstSeenAtBetween(Boolean verified, Date start, Date end, Pageable pageable);
    Page<EventDTO> findByVerifiedIsNullAndFirstSeenAtBetween(Date start, Date end, Pageable pageable);
    Page<EventDTO> findByCreatedByAndVerifiedIsNullAndFirstSeenAtBetween(
            String createdBy,
            Date start,
            Date end,
            Pageable pageable);
    Page<EventDTO> findByCreatedByAndVerifiedAndFirstSeenAtBetween(
            String createdBy,
            Boolean verified,
            Date start,
            Date end,
            Pageable pageable);

    default Page<EventDTO> findByCreatedBy(String createdBy, Date start, Date end, Pageable pageable) {
        if (start == null || end == null) {
            return findByCreatedBy(createdBy, pageable);
        }
        return findByCreatedByAndFirstSeenAtBetween(createdBy, start, end, pageable);
    }

    default Page<EventDTO> findByVerified(Boolean verified, Date start, Date end, Pageable pageable) {
        if (start == null || end == null) {
            return findByVerified(verified, pageable);
        }
        return findByVerifiedAndFirstSeenAtBetween(verified, start, end, pageable);
    }

    default Page<EventDTO> findByVerifiedIsNull(Date start, Date end, Pageable pageable) {
        if (start == null || end == null) {
            return findByVerifiedIsNull(pageable);
        }
        return findByVerifiedIsNullAndFirstSeenAtBetween(start, end, pageable);
    }

    default Page<EventDTO> findByCreatedByAndVerifiedIsNull(
            String createdBy,
            Date start,
            Date end,
            Pageable pageable) {
        if (start == null || end == null) {
            return findByCreatedByAndVerifiedIsNull(createdBy, pageable);
        }
        return findByCreatedByAndVerifiedIsNullAndFirstSeenAtBetween(createdBy, start, end, pageable);
    }

    default Page<EventDTO> findByCreatedByAndVerified(
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
