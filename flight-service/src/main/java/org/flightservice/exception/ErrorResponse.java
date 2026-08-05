package org.flightservice.exception;
import lombok.Getter;

@Getter
public class ErrorResponse {
    private final String message;
    private final int status;
    private final String error;
    private final long timestamp;

    public ErrorResponse(String message, int status, String error) {
        this.message = message;
        this.status = status;
        this.error = error;
        this.timestamp = System.currentTimeMillis();
    }
}