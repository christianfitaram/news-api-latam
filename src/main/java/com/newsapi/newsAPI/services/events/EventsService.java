package com.newsapi.newsAPI.services.events;

import com.newsapi.newsAPI.models.PaginatedResponse;
import com.newsapi.newsAPI.models.event.Event;
import com.newsapi.newsAPI.models.event.EventDTO;
import com.newsapi.newsAPI.models.event.TimeToVerifyStats;
import org.springframework.data.domain.Pageable;

public interface EventsService {
    PaginatedResponse<EventDTO> getEvents(Pageable pageable);
    PaginatedResponse<Event> getLiteEvents(Pageable pageable);


    PaginatedResponse<EventDTO> getEventsByCategory(Pageable pageable, String category);
    PaginatedResponse<Event> getEventsLiteByCategory(Pageable pageable, String category);

    PaginatedResponse<EventDTO> getEventsByDate(Pageable pageable, String date);
    PaginatedResponse<Event> getEventsLiteByDate(Pageable pageable, String date);

    PaginatedResponse<EventDTO> getEventsByStatus(Pageable pageable, String status);
    PaginatedResponse<Event> getEventsLiteByStatus(Pageable pageable, String status);

    PaginatedResponse<EventDTO> isEventVerified(Pageable pageable, Boolean verified, String start, String end);
    PaginatedResponse<Event> isEventLiteVerfied(Pageable pageable, Boolean verified, String start, String end);
    PaginatedResponse<EventDTO> isEventVerifiedNull(Pageable pageable, String start, String end);
    PaginatedResponse<Event> isEventLiteVerifiedNull(Pageable pageable, String start, String end);
    PaginatedResponse<EventDTO> getEventsByCreatedByAndVerifiedNull(Pageable pageable, String createdBy, String start, String end);
    PaginatedResponse<Event> getEventsLiteByCreatedByAndVerifiedNull(Pageable pageable, String createdBy, String start, String end);
    
    PaginatedResponse<EventDTO> getEventsCreatedBy(Pageable pageable, String createdBy, String start, String end);
    PaginatedResponse<Event> getEventsLiteCreatedBy(Pageable pageable, String createdBy, String start, String end);
    PaginatedResponse<EventDTO> getEventsByCreatedByAndVerified(Pageable pageable, String createdBy, Boolean verified, String start, String end);
    PaginatedResponse<Event> getEventsLiteByCreatedByAndVerified(Pageable pageable, String createdBy, Boolean verified, String start, String end);
    PaginatedResponse<EventDTO> searchEvents(Pageable pageable, String label, String summary, String start, String end, String verified);
    TimeToVerifyStats getAverageTimeToVerify(String createdBy, String start, String end);
    
    Event getLiteEventById(String id);
    EventDTO getEventById(String id);


    EventDTO createEvent(EventDTO event);
    EventDTO updateEvent(String id, EventDTO event);
    EventDTO deleteEvent(String id);
 
}
