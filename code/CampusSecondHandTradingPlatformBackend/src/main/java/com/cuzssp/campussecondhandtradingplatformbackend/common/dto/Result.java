package com.cuzssp.campussecondhandtradingplatformbackend.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    private Integer code;
    private String message;
    private T data;

    public static class Code {
        public final static Integer SUCCESS = 200;
        public final static Integer BAD_REQUEST = 400;
        public final static Integer UNAUTHORIZED = 401;
        public final static Integer FORBIDDEN = 403;
        public final static Integer NOT_FOUND = 404;
        public final static Integer METHOD_NOT_ALLOWED = 405;
        public final static Integer REQUEST_ENTITY_TOO_LARGE = 413;
        public final static Integer INTERNAL_ERROR = 500;
    }

    public static <T> Result<T> success() {
        return new Result<>(Code.SUCCESS, "success", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(Code.SUCCESS, "success", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(Code.SUCCESS, message, data);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(Code.INTERNAL_ERROR, message, null);
    }

}
