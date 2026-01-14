package com.newsapi.newsAPI.services.events;

import com.newsapi.newsAPI.exceptions.ResourceNotFoundException;
import com.newsapi.newsAPI.models.PaginatedResponse;
import com.newsapi.newsAPI.models.event.Event;
import com.newsapi.newsAPI.models.event.EventDTO;
import com.newsapi.newsAPI.models.event.TimeToVerifyStats;
import com.newsapi.newsAPI.repositories.EventRepository;
import com.newsapi.newsAPI.repositories.EventsRepository;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Date;

@Service
public class EventsServiceImpl implements EventsService {
    private final EventsRepository eventsRepository;
    private final EventRepository eventRepository;
    private final MongoTemplate mongoTemplate;

    public EventsServiceImpl(EventsRepository eventsRepository, EventRepository eventRepository,
            MongoTemplate mongoTemplate) {
        this.eventsRepository = eventsRepository;
        this.eventRepository = eventRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public PaginatedResponse<EventDTO> getEvents(Pageable pageable) {
        Page<EventDTO> eventPage = eventsRepository.findAll(pageable);
        return new PaginatedResponse<>(
                eventPage.getContent(),
                (int) eventPage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize());
    }

    @Override
    public PaginatedResponse<Event> getLiteEvents(Pageable pageable) {
        Page<Event> eventPage = eventRepository.findAll(pageable);
        return new PaginatedResponse<>(
                eventPage.getContent(),
                (int) eventPage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize());
    }

    @Override
    public PaginatedResponse<EventDTO> getEventsByCategory(Pageable pageable, String category) {
        Page<EventDTO> eventPage = eventsRepository.findByCategory(category, pageable);
        return new PaginatedResponse<>(
                eventPage.getContent(),
                (int) eventPage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize());
    }

    @Override
    public PaginatedResponse<Event> getEventsLiteByCategory(Pageable pageable, String category) {
        Page<Event> eventPage = eventRepository.findByCategory(category, pageable);
        return new PaginatedResponse<>(
                eventPage.getContent(),
                (int) eventPage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize());
    }

    @Override
    public PaginatedResponse<EventDTO> getEventsByDate(Pageable pageable, String date) {
        LocalDate localDate = LocalDate.parse(date); // expects "yyyy-MM-dd"
        Date start = java.sql.Date.valueOf(localDate);
        Date end = java.sql.Date.valueOf(localDate.plusDays(1));
        Page<EventDTO> eventPage = eventsRepository.findByFirstSeenAtBetween(start, end, pageable);
        return new PaginatedResponse<>(
                eventPage.getContent(),
                (int) eventPage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize());
    }

    @Override
    public PaginatedResponse<Event> getEventsLiteByDate(Pageable pageable, String date) {
        LocalDate localDate = LocalDate.parse(date); // expects "yyyy-MM-dd"
        Date start = java.sql.Date.valueOf(localDate);
        Date end = java.sql.Date.valueOf(localDate.plusDays(1));
        Page<Event> eventPage = eventRepository.findByFirstSeenAtBetween(start, end, pageable);
        return new PaginatedResponse<>(
                eventPage.getContent(),
                (int) eventPage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize());
    }

    @Override
    public PaginatedResponse<EventDTO> getEventsByStatus(Pageable pageable, String status) {
        Page<EventDTO> eventPage = eventsRepository.findByStatus(status, pageable);
        return new PaginatedResponse<>(
                eventPage.getContent(),
                (int) eventPage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize());
    }

    @Override
    public PaginatedResponse<Event> getEventsLiteByStatus(Pageable pageable, String status) {
        Page<Event> eventPage = eventRepository.findByStatus(status, pageable);
        return new PaginatedResponse<>(
                eventPage.getContent(),
                (int) eventPage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize());
    }

    @Override
    public EventDTO getEventById(String id) {
        return eventsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
    }

    @Override
    public Event getLiteEventById(String id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lite event not found with id: " + id));
    }

    @Override
    public EventDTO createEvent(EventDTO event) {
        updateTimeToVerifyIfVerified(event);
        return eventsRepository.save(event);
    }

    // Partial update: only non-null fields in 'updated' will overwrite existing
    // fields
    @Override
    public EventDTO updateEvent(String id, EventDTO updated) {
        EventDTO existing = eventsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

        if (updated.getLabel() != null)
            existing.setLabel(updated.getLabel());
        if (updated.getSummary() != null)
            existing.setSummary(updated.getSummary());
        if (updated.getCategory() != null)
            existing.setCategory(updated.getCategory());
        if (updated.getLanguage() != null)
            existing.setLanguage(updated.getLanguage());
        if (updated.getLocation() != null)
            existing.setLocation(updated.getLocation());
        if (updated.getLocationConfidence() != null)
            existing.setLocationConfidence(updated.getLocationConfidence());
        if (updated.getLocationGranularity() != null)
            existing.setLocationGranularity(updated.getLocationGranularity());
        if (updated.getLocationSource() != null)
            existing.setLocationSource(updated.getLocationSource());
        if (updated.getLat() != null)
            existing.setLat(updated.getLat());
        if (updated.getLng() != null)
            existing.setLng(updated.getLng());
        if (updated.getUrl() != null)
            existing.setUrl(updated.getUrl());
        if (updated.getCountry() != null)
            existing.setCountry(updated.getCountry());
        if (updated.getRegion() != null)
            existing.setRegion(updated.getRegion());
        if (updated.getSource() != null)
            existing.setSource(updated.getSource());
        if (updated.getTags() != null)
            existing.setTags(updated.getTags());
        if (updated.getCentroidEmbedding() != null)
            existing.setCentroidEmbedding(updated.getCentroidEmbedding());
        if (updated.getFirstSeenAt() != null)
            existing.setFirstSeenAt(updated.getFirstSeenAt());
        if (updated.getLastSeenAt() != null)
            existing.setLastSeenAt(updated.getLastSeenAt());
        if (updated.getArticlesPreview() != null)
            existing.setArticlesPreview(updated.getArticlesPreview());
        if (updated.getImportanceScore() != null)
            existing.setImportanceScore(updated.getImportanceScore());
        if (updated.getStatus() != null)
            existing.setStatus(updated.getStatus());
        if (updated.getSourceStats() != null)
            existing.setSourceStats(updated.getSourceStats());
        if (updated.getMiscFlags() != null)
            existing.setMiscFlags(updated.getMiscFlags());
        if (updated.getVerified() != null)
            existing.setVerified(updated.getVerified());
        if (updated.getVerifiedAt() != null)
            existing.setVerifiedAt(updated.getVerifiedAt());
        if (updated.getCreatedBy() != null)
            existing.setCreatedBy(updated.getCreatedBy());
        if (updated.getDateByHuman() != null)
            existing.setDateByHuman(updated.getDateByHuman());
        if (updated.getContextualBriefing() != null)
            existing.setContextualBriefing(updated.getContextualBriefing());
        updateTimeToVerifyIfVerified(existing);
        return eventsRepository.save(existing);
    }

    @Override
    public EventDTO deleteEvent(String id) {
        EventDTO existing = eventsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        eventsRepository.delete(existing);
        return existing;
    }

    @Override
    public PaginatedResponse<EventDTO> isEventVerified(Pageable pageable, Boolean verified, String start, String end) {
        Date startDate = parseStartDate(start);
        Date endDate = parseEndDate(end);
        Page<EventDTO> eventPage = eventsRepository.findByVerified(verified, startDate, endDate, pageable);
        return new PaginatedResponse<>(
                eventPage.getContent(),
                (int) eventPage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize());
    }

    @Override
    public PaginatedResponse<Event> isEventLiteVerfied(Pageable pageable, Boolean verified, String start, String end) {
        Date startDate = parseStartDate(start);
        Date endDate = parseEndDate(end);
        Page<Event> eventPage = eventRepository.findByVerified(verified, startDate, endDate, pageable);
        return new PaginatedResponse<>(
                eventPage.getContent(),
                (int) eventPage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize());
    }

    @Override
    public PaginatedResponse<EventDTO> isEventVerifiedNull(Pageable pageable, String start, String end) {
        Date startDate = parseStartDate(start);
        Date endDate = parseEndDate(end);
        Page<EventDTO> eventPage = eventsRepository.findByVerifiedIsNull(startDate, endDate, pageable);
        return new PaginatedResponse<>(
                eventPage.getContent(),
                (int) eventPage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize());
    }

    @Override
    public PaginatedResponse<Event> isEventLiteVerifiedNull(Pageable pageable, String start, String end) {
        Date startDate = parseStartDate(start);
        Date endDate = parseEndDate(end);
        Page<Event> eventPage = eventRepository.findByVerifiedIsNull(startDate, endDate, pageable);
        return new PaginatedResponse<>(
                eventPage.getContent(),
                (int) eventPage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize());
    }

    @Override
    public PaginatedResponse<EventDTO> getEventsByCreatedByAndVerifiedNull(Pageable pageable, String createdBy,
            String start, String end) {
        Date startDate = parseStartDate(start);
        Date endDate = parseEndDate(end);
        Page<EventDTO> eventPage = eventsRepository.findByCreatedByAndVerifiedIsNull(createdBy, startDate, endDate,
                pageable);
        return new PaginatedResponse<>(
                eventPage.getContent(),
                (int) eventPage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize());
    }

    @Override
    public PaginatedResponse<Event> getEventsLiteByCreatedByAndVerifiedNull(Pageable pageable, String createdBy,
            String start, String end) {
        Date startDate = parseStartDate(start);
        Date endDate = parseEndDate(end);
        Page<Event> eventPage = eventRepository.findByCreatedByAndVerifiedIsNull(createdBy, startDate, endDate,
                pageable);
        return new PaginatedResponse<>(
                eventPage.getContent(),
                (int) eventPage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize());
    }

    @Override
    public PaginatedResponse<EventDTO> getEventsCreatedBy(Pageable pageable, String createdBy, String start,
            String end) {
        Date startDate = parseStartDate(start);
        Date endDate = parseEndDate(end);
        Page<EventDTO> eventPage = eventsRepository.findByCreatedBy(createdBy, startDate, endDate, pageable);
        return new PaginatedResponse<>(
                eventPage.getContent(),
                (int) eventPage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize());
    }

    @Override
    public PaginatedResponse<Event> getEventsLiteCreatedBy(Pageable pageable, String createdBy, String start,
            String end) {
        Date startDate = parseStartDate(start);
        Date endDate = parseEndDate(end);
        Page<Event> eventPage = eventRepository.findByCreatedBy(createdBy, startDate, endDate, pageable);
        return new PaginatedResponse<>(
                eventPage.getContent(),
                (int) eventPage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize());
    }

    @Override
    public PaginatedResponse<EventDTO> getEventsByCreatedByAndVerified(Pageable pageable, String createdBy,
            Boolean verified, String start, String end) {
        Date startDate = parseStartDate(start);
        Date endDate = parseEndDate(end);
        Page<EventDTO> eventPage = eventsRepository.findByCreatedByAndVerified(createdBy, verified, startDate, endDate,
                pageable);
        return new PaginatedResponse<>(
                eventPage.getContent(),
                (int) eventPage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize());
    }

    @Override
    public PaginatedResponse<Event> getEventsLiteByCreatedByAndVerified(Pageable pageable, String createdBy,
            Boolean verified, String start, String end) {
        Date startDate = parseStartDate(start);
        Date endDate = parseEndDate(end);
        Page<Event> eventPage = eventRepository.findByCreatedByAndVerified(createdBy, verified, startDate, endDate,
                pageable);
        return new PaginatedResponse<>(
                eventPage.getContent(),
                (int) eventPage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize());
    }

    @Override
    public TimeToVerifyStats getAverageTimeToVerify(String createdBy, String start, String end) {
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("verified").is(true));
        criteriaList.add(Criteria.where("verified_at").exists(true).ne(null));
        criteriaList.add(Criteria.where("time_to_verify").exists(true).ne(null));

        if (createdBy != null && !createdBy.isBlank()) {
            criteriaList.add(Criteria.where("created_by").is(createdBy.trim()));
        }

        Date startDate = parseStartDate(start);
        Date endDate = parseEndDate(end);
        if (startDate != null) {
            criteriaList.add(Criteria.where("first_seen_at").gte(startDate));
        }
        if (endDate != null) {
            criteriaList.add(Criteria.where("first_seen_at").lt(endDate));
        }

        GroupOperation group = Aggregation.group()
                .avg("time_to_verify").as("avgSeconds")
                .count().as("totalItems");

        Aggregation aggregation;
        if (criteriaList.isEmpty()) {
            aggregation = Aggregation.newAggregation(group);
        } else {
            Criteria matchCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
            MatchOperation match = Aggregation.match(matchCriteria);
            aggregation = Aggregation.newAggregation(match, group);
        }
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "events", Document.class);
        Document doc = results.getUniqueMappedResult();

        double averageSeconds = 0.0;
        long totalItems = 0L;
        if (doc != null) {
            Number avg = doc.get("avgSeconds", Number.class);
            if (avg != null) {
                averageSeconds = avg.doubleValue();
            }
            Number total = doc.get("totalItems", Number.class);
            if (total != null) {
                totalItems = total.longValue();
            }
        }

        return new TimeToVerifyStats(averageSeconds, totalItems);
    }

    private Date parseStartDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return java.sql.Date.valueOf(LocalDate.parse(value));
    }

    private Date parseEndDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return java.sql.Date.valueOf(LocalDate.parse(value).plusDays(1));
    }

    private void updateTimeToVerifyIfVerified(EventDTO event) {
        if (!Boolean.TRUE.equals(event.getVerified())) {
            return;
        }
        Date verifiedAt = event.getVerifiedAt();
        Date firstSeenAt = event.getFirstSeenAt();
        if (verifiedAt == null || firstSeenAt == null) {
            return;
        }
        long seconds = Duration.between(firstSeenAt.toInstant(), verifiedAt.toInstant()).toSeconds();
        event.setTimeToVerify(seconds);
    }

    @Override
    public PaginatedResponse<EventDTO> searchEvents(Pageable pageable, String label, String summary, String start,
            String end, String verified) {
        Query query = new Query();
        List<Criteria> criteria = new ArrayList<>();

        // label filter (case-insensitive "contains")
        if (label != null && !label.isBlank()) {
            Pattern p = Pattern.compile(Pattern.quote(label.trim()), Pattern.CASE_INSENSITIVE);
            criteria.add(Criteria.where("label").regex(p));
        }

        // summary filter (case-insensitive "contains")
        if (summary != null && !summary.isBlank()) {
            Pattern p = Pattern.compile(Pattern.quote(summary.trim()), Pattern.CASE_INSENSITIVE);
            criteria.add(Criteria.where("summary").regex(p));
        }

        // date filters (compared against firstSeenAt)
        // start_date: inclusive (>= start at 00:00)
        if (start != null && !start.isBlank()) {
            LocalDate sd = LocalDate.parse(start.trim()); // expects yyyy-MM-dd
            Date startDate = java.sql.Date.valueOf(sd);
            criteria.add(Criteria.where("firstSeenAt").gte(startDate));
        }

        // end_date: inclusive by day (we do < (end + 1 day))
        if (end != null && !end.isBlank()) {
            LocalDate ed = LocalDate.parse(end.trim()); // expects yyyy-MM-dd
            Date endExclusive = java.sql.Date.valueOf(ed.plusDays(1));
            criteria.add(Criteria.where("firstSeenAt").lt(endExclusive));
        }

        if (verified != null && !verified.isBlank()) {
            String v = verified.trim().toLowerCase();

            if ("true".equals(v) || "false".equals(v)) {
                criteria.add(Criteria.where("verified").is(Boolean.parseBoolean(v)));
            } else if ("null".equals(v)) {
                // Search documents where "verified" field is missing
                criteria.add(Criteria.where("verified").exists(false));
            } else {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid verified value. Use: true, false, null");
            }
        }

        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }

        long total = mongoTemplate.count(query, EventDTO.class);

        query.with(pageable);
        List<EventDTO> results = mongoTemplate.find(query, EventDTO.class);

        return new PaginatedResponse<>(
                results,
                (int) total,
                pageable.getPageNumber(),
                pageable.getPageSize());
    }

}
