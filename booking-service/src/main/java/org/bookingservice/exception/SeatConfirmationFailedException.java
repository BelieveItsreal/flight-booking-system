package org.bookingservice.exception;

public class SeatConfirmationFailedException extends RuntimeException {
    public SeatConfirmationFailedException(String message) {
        super(message);
    }
}
