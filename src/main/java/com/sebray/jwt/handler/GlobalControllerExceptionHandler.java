package com.sebray.jwt.handler;

import com.sebray.jwt.dto.ErrorDto;
import com.sebray.jwt.exception.AuthException;
import com.sebray.jwt.exception.ResourceAlreadyExistsException;
import com.sebray.jwt.exception.ResourceNotFoundException;
import com.sebray.jwt.exception.TokenRefreshException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Calendar;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {
    @ExceptionHandler(value = TokenRefreshException.class)
    public ResponseEntity<ErrorDto> handleTokenRefreshException(TokenRefreshException ex) {
        return new ResponseEntity<>(
                new ErrorDto(ex.getMessage(),
                        Calendar.getInstance().getTime()),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = AuthException.class)
    public ResponseEntity<ErrorDto> handleAuthException(AuthException ex) {
        return new ResponseEntity<>(
                new ErrorDto(ex.getMessage(),
                        Calendar.getInstance().getTime()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorDto> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        return new ResponseEntity<>(
                new ErrorDto(ex.getMessage(),
                        Calendar.getInstance().getTime()),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<ErrorDto> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(
                new ErrorDto(ex.getMessage(),
                        Calendar.getInstance().getTime()),
                HttpStatus.NOT_FOUND);
    }
}
