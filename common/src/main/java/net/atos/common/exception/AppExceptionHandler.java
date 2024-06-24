package net.atos.common.exception;

import net.atos.common.response.ApiResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Qualifier("commonAppExceptionHandler")
public class AppExceptionHandler {
    private ResponseEntity<Object> buildErrorResponse(String message, HttpStatus status) {
        ApiResponse<Object> response = ApiResponse.builder()
                .error(message)
                .timestamp(LocalDateTime.now())
                .code(status.value())
                .build();
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(value = {RessourceNotFoundException.class})
    public ResponseEntity<Object> handleResourceNotFoundException(RessourceNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {RessourceAlreadyExistsException.class})
    public ResponseEntity<Object> handleResourceAlreadyExistsException(RessourceAlreadyExistsException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .data(errors)
                .error(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .code(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .build();
        return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
