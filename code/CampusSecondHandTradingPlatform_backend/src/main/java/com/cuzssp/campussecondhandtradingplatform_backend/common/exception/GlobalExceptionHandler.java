package com.cuzssp.campussecondhandtradingplatform_backend.common.exception;

import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<?>> handleBusinessException(BusinessException e) {
        log.warn("Business exception: {}", e.getMessage());
        return ResponseEntity.status(e.getCode())
                .body(Result.error(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Result<?>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.error(400, "Upload file size exceeds limit"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<?>> handleException(Exception e) {
        log.error("System exception", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error(500, "Internal server error"));
    }
}