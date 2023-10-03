package com.user_service.formater;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TimeFormatter {
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");


    public String format(LocalDate localDate) {
        return localDate == null ? null : localDate.format(dateFormatter);
    }


    public String format (LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.format(dateTimeFormatter);
    }

}
