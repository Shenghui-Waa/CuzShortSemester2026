package com.cuzssp.campussecondhandtradingplatformbackend.common.exception;

import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.Result;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 业务异常
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<?>> handleBusinessException(BusinessException e) {
        log.warn("Business exception: {}", e.getMessage());
        return ResponseEntity.status(e.getCode())
                .body(Result.error(e.getCode(), e.getMessage()));
    }

    // 最大上传大小异常
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Result<?>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.error(Result.Code.BAD_REQUEST, "Upload file size exceeds limit"));
    }

    // 通用异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<?>> handleException(Exception e) {
        log.error("System exception", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error("Internal server error"));
    }

    // 缺少请求参数
    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MissingServletRequestPartException.class,
            MissingRequestHeaderException.class
    })
    public ResponseEntity<Result<?>> handleMissingRequestData(
            Exception exception
    ) {
        return badRequest("Required request data is missing");
    }

    // DTO 校验失败
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<?>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception
    ) {
        return badRequest("Request validation failed");
    }

    // JSON 请求体格式错误
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result<?>> handleUnreadableMessage(
            HttpMessageNotReadableException exception
    ) {
        return badRequest("Request body is invalid");
    }

    // 参数类型错误
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result<?>> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception
    ) {
        return badRequest("Request parameter type is invalid");
    }

    // 方法不支持
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Result<?>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException exception
    ) {
        ResponseEntity.BodyBuilder builder = ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED);

        if (exception.getSupportedHttpMethods() != null) {
            builder.header(
                    "Allow",
                    exception.getSupportedHttpMethods()
                            .stream()
                            .map(HttpMethod::name)
                            .sorted()
                            .collect(Collectors.joining(", "))
            );
        }

        return builder.body(Result.error(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                "HTTP method is not supported"
        ));
    }

    // Bean Validation 约束异常
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<?>> handleConstraintViolation(
            ConstraintViolationException exception
    ) {
        return badRequest("Request validation failed");
    }

    // 统一 400 方法
    private ResponseEntity<Result<?>> badRequest(String message) {
        return ResponseEntity
                .status(Result.Code.BAD_REQUEST)
                .body(Result.error(Result.Code.BAD_REQUEST, message));
    }

}