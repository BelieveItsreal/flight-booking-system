package org.emailservice.controller;

import jakarta.validation.Valid;
import org.emailservice.dto.BookingConfirmationRequest;
import org.emailservice.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/emails")
public class EmailController {
    private final EmailService emailService;
    public EmailController(EmailService emailService){
        this.emailService = emailService;
    }
    @PostMapping("/booking-confirmation")
    public ResponseEntity<Void> sendBookingConfirmation(@Valid @RequestBody BookingConfirmationRequest request){
        emailService.sendBookingConfirmation(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
