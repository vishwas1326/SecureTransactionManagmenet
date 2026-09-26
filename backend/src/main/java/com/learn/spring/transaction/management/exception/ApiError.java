package com.learn.spring.transaction.management.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiError(
        LocalDateTime timeStamp,
        int status,
        String message,
        Map<String,String> validationErrors
){
}
