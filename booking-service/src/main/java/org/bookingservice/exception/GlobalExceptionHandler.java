package org.bookingservice.exception;

import java.util.stream.Collectors;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookingNotFoundException(BookingNotFoundException ex){
        ErrorResponse error = new ErrorResponse(
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value(),
            "Not Found"
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse error = new ErrorResponse(message, HttpStatus.BAD_REQUEST.value(), "Validation Failed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex){
        ErrorResponse error = new ErrorResponse(
            ex.getMessage(),
            HttpStatus.FORBIDDEN.value(),
            "unauthorized"
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(SeatConfirmationFailedException.class)
    public ResponseEntity<ErrorResponse> handleSeatConfirmedFailedException(SeatConfirmationFailedException ex){
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                "Conflict"
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(SeatUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleSeatUnavailableException(SeatUnavailableException ex){
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                "Conflict"
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(StatusRuntimeException.class)
    public ResponseEntity<ErrorResponse> handleGrpcException(StatusRuntimeException ex){
        if (ex.getStatus().getCode() == Status.Code.NOT_FOUND){
            ErrorResponse error = new ErrorResponse(
                    ex.getStatus().getDescription(),
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found"
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        ErrorResponse error = new ErrorResponse(
                "Flight service is currently unavailable, please try again later",
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Service Unavailable"
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }
}
