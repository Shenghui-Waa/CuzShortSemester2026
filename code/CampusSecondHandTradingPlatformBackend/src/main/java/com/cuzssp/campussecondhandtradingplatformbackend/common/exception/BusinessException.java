package com.cuzssp.campussecondhandtradingplatformbackend.common.exception;

import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.Result;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = Result.Code.BAD_REQUEST;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

}
