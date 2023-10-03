package com.user_service.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageSrc {
    private final MessageSource messageSource;

    public String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    public String getMessage (String code, Object object) {
        return messageSource.getMessage(code, new Object[]{object}, LocaleContextHolder.getLocale());
    }

}
