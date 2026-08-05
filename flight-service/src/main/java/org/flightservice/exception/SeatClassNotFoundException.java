package org.flightservice.exception;

public class SeatClassNotFoundException extends RuntimeException{
    public SeatClassNotFoundException (String message) {
        super(message);
    }
}
