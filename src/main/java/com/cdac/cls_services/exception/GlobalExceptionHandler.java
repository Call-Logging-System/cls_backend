package com.cdac.cls_services.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecordAlreadyExistsException.class)
    public ResponseEntity<ResponseDto> handleRecordAlreadyExistsException(RecordAlreadyExistsException e) {
        ResponseDto error = new ResponseDto("400", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<ResponseDto> handleRecordNotFoundException(RecordNotFoundException e) {
        ResponseDto error = new ResponseDto("404", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
