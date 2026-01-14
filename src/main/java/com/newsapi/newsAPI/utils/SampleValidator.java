package com.newsapi.newsAPI.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SampleValidator {

    private static final Pattern PATTERN = Pattern.compile(
            "^(\\d+)-(202[5-9]|20[3-9]\\d)-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$"
    );

    public static boolean isValidSample(String sample) {
        Matcher matcher = PATTERN.matcher(sample);
        if (!matcher.matches()) {
            return false;
        }

        String year = matcher.group(2);
        String month = matcher.group(3);
        String day = matcher.group(4);
        String dateStr = String.format("%s-%s-%s", year, month, day);

        try {
            LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
