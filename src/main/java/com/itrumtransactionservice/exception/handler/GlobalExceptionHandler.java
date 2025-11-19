package com.itrumtransactionservice.exception.handler;

import com.itrumtransactionservice.dto.ErrorResponse;
import com.itrumtransactionservice.exception.InvalidOperationTypeException;
import com.itrumtransactionservice.exception.WalletNotFoundException;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WalletNotFoundException.class)
    public ErrorResponse handleWalletNotFoundException(WalletNotFoundException e) {
        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidOperationTypeException.class)
    public ErrorResponse handleInvalidOperationTypeException(InvalidOperationTypeException e) {
        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(e.getMessage())
                .build();
    }
}