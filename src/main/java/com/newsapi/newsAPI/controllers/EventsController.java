package com.newsapi.newsAPI.controllers;

import com.newsapi.newsAPI.models.ApiResponse;
import com.newsapi.newsAPI.models.PaginatedResponse;
import com.newsapi.newsAPI.models.event.Event;
import com.newsapi.newsAPI.models.event.EventDeleteResponse;
import com.newsapi.newsAPI.models.event.EventDTO;
import com.newsapi.newsAPI.models.event.TimeToVerifyStats;
import com.newsapi.newsAPI.services.events.EventsService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/events")
@CrossOrigin(origins = "*")
public class EventsController {
    private final EventsService eventsService;

    public EventsController(EventsService eventsService) {
        this.eventsService = eventsService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<EventDTO>>> getPaginatedEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<EventDTO> events = eventsService.getEvents(pageable);
        return ResponseEntity.ok(ApiResponse.success("Paginated events", events));
    }

    @GetMapping("/lite")
    public ResponseEntity<ApiResponse<PaginatedResponse<Event>>> getLiteEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort) {
        Sort.Direction direction = Sort.Direction.fromOptionalString(sort).orElse(Sort.Direction.DESC);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(direction, "firstSeenAt"));
        PaginatedResponse<Event> events = eventsService.getLiteEvents(pageable);
        return ResponseEntity.ok(ApiResponse.success("Paginated lite events", events));
    }

    @GetMapping("/raw")
    public ResponseEntity<ApiResponse<PaginatedResponse<Event>>> getRawEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort) {
        Sort.Direction direction = Sort.Direction.fromOptionalString(sort).orElse(Sort.Direction.DESC);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(direction, "firstSeenAt"));
        PaginatedResponse<Event> events = eventsService.getLiteEvents(pageable);
        return ResponseEntity.ok(ApiResponse.success("Paginated lite events", events));
    }



    @GetMapping("/by-category")
    public ResponseEntity<ApiResponse<PaginatedResponse<EventDTO>>> getPaginatedEventsByCategory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam String value) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<EventDTO> events = eventsService.getEventsByCategory(pageable, value);
        return ResponseEntity.ok(ApiResponse.success("Paginated events by category", events));
    }
    @GetMapping("/lite/by-category")
    public ResponseEntity<ApiResponse<PaginatedResponse<Event>>> getPaginatedEventsLiteByCategory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam String value) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<Event> events = eventsService.getEventsLiteByCategory(pageable, value);
        return ResponseEntity.ok(ApiResponse.success("Paginated events by category", events));
    }

    @GetMapping("/by-date")
    public ResponseEntity<ApiResponse<PaginatedResponse<EventDTO>>> getPaginatedEventsByDate(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam String value) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<EventDTO> events = eventsService.getEventsByDate(pageable, value);
        return ResponseEntity.ok(ApiResponse.success("Paginated events by date", events));
    }

    @GetMapping("/lite/by-date")
    public ResponseEntity<ApiResponse<PaginatedResponse<Event>>> getPaginatedEventsLiteByDate(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam String value) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<Event> events = eventsService.getEventsLiteByDate(pageable, value);
        return ResponseEntity.ok(ApiResponse.success("Paginated events by date", events));
    }

    @GetMapping("/by-status")
    public ResponseEntity<ApiResponse<PaginatedResponse<EventDTO>>> getPaginatedEventsByStatus(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam String value) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<EventDTO> events = eventsService.getEventsByStatus(pageable, value);
        return ResponseEntity.ok(ApiResponse.success("Paginated events by status", events));
    }

    @GetMapping("/lite/by-status")
    public ResponseEntity<ApiResponse<PaginatedResponse<Event>>> getPaginatedEventsLiteByStatus(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam String value) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<Event> events = eventsService.getEventsLiteByStatus(pageable, value);
        return ResponseEntity.ok(ApiResponse.success("Paginated events by status", events));
    }

    @GetMapping("/by-created-by-and-verified")
    public ResponseEntity<ApiResponse<PaginatedResponse<EventDTO>>> getPaginatedEventsByCreatedByAndVerified(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam(name = "created_by") String createdBy,
            @RequestParam Boolean verified,
            @RequestParam(name = "start_date", required = false) String startDate,
            @RequestParam(name = "end_date", required = false) String endDate) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<EventDTO> events = eventsService.getEventsByCreatedByAndVerified(
                pageable,
                createdBy,
                verified,
                startDate,
                endDate);
        return ResponseEntity.ok(ApiResponse.success("Paginated events by createdBy and verified", events));
    }

    @GetMapping("/lite/by-created-by-and-verified")
    public ResponseEntity<ApiResponse<PaginatedResponse<Event>>> getPaginatedEventsLiteByCreatedByAndVerified(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam(name = "created_by") String createdBy,
            @RequestParam Boolean verified,
            @RequestParam(name = "start_date", required = false) String startDate,
            @RequestParam(name = "end_date", required = false) String endDate) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<Event> events = eventsService.getEventsLiteByCreatedByAndVerified(
                pageable,
                createdBy,
                verified,
                startDate,
                endDate);
        return ResponseEntity.ok(ApiResponse.success("Paginated events by createdBy and verified", events));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PaginatedResponse<EventDTO>>> searchEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam(required = false) String label,
            @RequestParam(required = false) String summary,
            @RequestParam(name = "start_date", required = false) String startDate,
            @RequestParam(name = "end_date", required = false) String endDate,
            @RequestParam(required = false) String verified 
    ) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<EventDTO> events = eventsService.searchEvents(pageable, label, summary, startDate, endDate, verified);
        return ResponseEntity.ok(ApiResponse.success("Search events", events));
    }


    @GetMapping("/by-created-by-and-verified-null")
    public ResponseEntity<ApiResponse<PaginatedResponse<EventDTO>>> getPaginatedEventsByCreatedByAndVerifiedNull(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam(name = "created_by") String createdBy,
            @RequestParam(name = "start_date", required = false) String startDate,
            @RequestParam(name = "end_date", required = false) String endDate) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<EventDTO> events = eventsService.getEventsByCreatedByAndVerifiedNull(
                pageable,
                createdBy,
                startDate,
                endDate);
        return ResponseEntity.ok(ApiResponse.success("Paginated events by createdBy with null verified", events));
    }

    @GetMapping("/lite/by-created-by-and-verified-null")
    public ResponseEntity<ApiResponse<PaginatedResponse<Event>>> getPaginatedEventsLiteByCreatedByAndVerifiedNull(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam(name = "created_by") String createdBy,
            @RequestParam(name = "start_date", required = false) String startDate,
            @RequestParam(name = "end_date", required = false) String endDate) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<Event> events = eventsService.getEventsLiteByCreatedByAndVerifiedNull(
                pageable,
                createdBy,
                startDate,
                endDate);
        return ResponseEntity.ok(ApiResponse.success("Paginated events by createdBy with null verified", events));
    }
    
    @GetMapping("/by-created-by")
    public ResponseEntity<ApiResponse<PaginatedResponse<EventDTO>>> getPaginatedEventsByCreatedBy(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam(name = "value") String createdBy,
            @RequestParam(name = "start_date", required = false) String startDate,
            @RequestParam(name = "end_date", required = false) String endDate) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<EventDTO> events = eventsService.getEventsCreatedBy(
                pageable,
                createdBy,
                startDate,
                endDate);
        return ResponseEntity.ok(ApiResponse.success("Paginated events by createdBy", events));
    }   

    @GetMapping("/lite/by-created-by")
    public ResponseEntity<ApiResponse<PaginatedResponse<Event>>> getPaginatedEventsLiteByCreatedBy(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam(name = "value") String createdBy,
            @RequestParam(name = "start_date", required = false) String startDate,
            @RequestParam(name = "end_date", required = false) String endDate) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<Event> events = eventsService.getEventsLiteCreatedBy(
                pageable,
                createdBy,
                startDate,
                endDate);
        return ResponseEntity.ok(ApiResponse.success("Paginated events by createdBy", events));
    }
    
    @GetMapping("/is-verified")
    public ResponseEntity<ApiResponse<PaginatedResponse<EventDTO>>> isEventVerified(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam(name = "value") Boolean verified,
            @RequestParam(name = "start_date", required = false) String startDate,
            @RequestParam(name = "end_date", required = false) String endDate) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<EventDTO> events = eventsService.isEventVerified(
                pageable,
                verified,
                startDate,
                endDate);
        return ResponseEntity.ok(ApiResponse.success("Paginated events by verified status", events));
    }

    @GetMapping("/lite/is-verified")
    public ResponseEntity<ApiResponse<PaginatedResponse<Event>>> isEventLiteVerfied(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam(name = "value") Boolean verified,
            @RequestParam(name = "start_date", required = false) String startDate,
            @RequestParam(name = "end_date", required = false) String endDate) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<Event> events = eventsService.isEventLiteVerfied(
                pageable,
                verified,
                startDate,
                endDate);
        return ResponseEntity.ok(ApiResponse.success("Paginated events by verified status", events));
    }

    @GetMapping("/is-verified-null")
    public ResponseEntity<ApiResponse<PaginatedResponse<EventDTO>>> isEventVerifiedNull(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam(name = "start_date", required = false) String startDate,
            @RequestParam(name = "end_date", required = false) String endDate) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<EventDTO> events = eventsService.isEventVerifiedNull(pageable, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Paginated events with null verified", events));
    }

    @GetMapping("/lite/is-verified-null")
    public ResponseEntity<ApiResponse<PaginatedResponse<Event>>> isEventLiteVerifiedNull(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam(name = "start_date", required = false) String startDate,
            @RequestParam(name = "end_date", required = false) String endDate) {
        PageRequest pageable = buildPageRequest(page, size, sort);
        PaginatedResponse<Event> events = eventsService.isEventLiteVerifiedNull(pageable, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Paginated events with null verified", events));
    }

    @GetMapping("/avg-time-to-verify")
    public ResponseEntity<ApiResponse<TimeToVerifyStats>> getAverageTimeToVerify(
            @RequestParam(name = "created_by", required = false) String createdBy,
            @RequestParam(name = "start_date", required = false) String startDate,
            @RequestParam(name = "end_date", required = false) String endDate) {
        TimeToVerifyStats stats = eventsService.getAverageTimeToVerify(createdBy, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Average timeToVerify (seconds)", stats));
    }
    

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventDTO>> getEventById(@PathVariable String id) {
        EventDTO event = eventsService.getEventById(id);
        return ResponseEntity.ok(ApiResponse.success("Event details", event));
    }


    @GetMapping("/lite/{id}")
    public ResponseEntity<ApiResponse<Event>> getLiteEventById(@PathVariable String id) {
        Event event = eventsService.getLiteEventById(id);
        return ResponseEntity.ok(ApiResponse.success("Lite event details", event));
    }


    @GetMapping("/raw/{id}")
    public ResponseEntity<ApiResponse<Event>> getRawEventById(@PathVariable String id) {
        Event event = eventsService.getLiteEventById(id);
        return ResponseEntity.ok(ApiResponse.success("Lite event details", event));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EventDTO>> createEvent(@RequestBody EventDTO event) {
        EventDTO saved = eventsService.createEvent(event);
        return ResponseEntity.ok(ApiResponse.success("Event created", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EventDTO>> updateEvent(@PathVariable String id, @RequestBody EventDTO event) {
        EventDTO updated = eventsService.updateEvent(id, event);
        return ResponseEntity.ok(ApiResponse.success("Event updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<EventDeleteResponse>> deleteEvent(@PathVariable String id) {
        EventDTO deleted = eventsService.deleteEvent(id);
        EventDeleteResponse payload = new EventDeleteResponse(deleted.getId(), deleted.getLabel());
        return ResponseEntity.ok(ApiResponse.success("Event deleted", payload));
    }

    private PageRequest buildPageRequest(int page, int size, String sort) {
        Sort.Direction direction = Sort.Direction.fromOptionalString(sort).orElse(Sort.Direction.DESC);
        return PageRequest.of(page, size, Sort.by(direction, "firstSeenAt"));
    }
}
