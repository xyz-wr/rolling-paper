package com.wrbread.roll.rollingpaper.advice;

import com.wrbread.roll.rollingpaper.exception.ExceptionCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class ErrorResponse {
    private int status;
    private String message;
    private HttpStatus httpStatus;


    private ErrorResponse(int status, String message, HttpStatus httpStatus) {
        this.status = status;
        this.message = message;
        this.httpStatus = httpStatus;
    }


    public static ErrorResponse of(ExceptionCode exceptionCode) {
        return new ErrorResponse(exceptionCode.getStatus(), exceptionCode.getMessage(), exceptionCode.getHttpStatus());
    }
}