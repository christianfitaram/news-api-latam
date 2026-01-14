package com.newsapi.newsAPI.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

@Configuration
public class MongoConfig {

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(List.of(new StringToDateConverter()));
    }

    @ReadingConverter
    static class StringToDateConverter implements Converter<String, Date> {
        @Override
        public Date convert(String source) {
            if (source == null) {
                return null;
            }
            String value = source.trim();
            if (value.isEmpty()) {
                return null;
            }
            try {
                return Date.from(OffsetDateTime.parse(value).toInstant());
            } catch (DateTimeParseException ignored) {
                // Fall through to alternate formats.
            }
            try {
                return Date.from(Instant.parse(value));
            } catch (DateTimeParseException ignored) {
                // Fall through to alternate formats.
            }
            try {
                return Date.from(LocalDateTime.parse(value).atZone(ZoneOffset.UTC).toInstant());
            } catch (DateTimeParseException ignored) {
                // Fall through to alternate formats.
            }
            try {
                return Date.from(LocalDate.parse(value).atStartOfDay(ZoneOffset.UTC).toInstant());
            } catch (DateTimeParseException ignored) {
                // Fall through to epoch millis.
            }
            try {
                long epochMillis = Long.parseLong(value);
                return new Date(epochMillis);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
    }
}
