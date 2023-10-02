package com.user_service.formater;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class TimeFormatter {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public LocalDate parseLocalDate (String date) {
        return LocalDate.parse(date, formatter);
    }

    public String format(LocalDate birthday) {
        return birthday == null ? null : birthday.format(formatter);
    }

}
