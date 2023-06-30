package com.young.asow.exception;

import com.young.asow.response.RestResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandle {

    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<Object> handleBusinessException(BusinessException ex) {
        RestResponse restResponse = RestResponse.fail(ex.getMessage());
        return new ResponseEntity(restResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        RestResponse restResponse = RestResponse.fail(ex.getMessage());
        return new ResponseEntity(restResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ...
}
