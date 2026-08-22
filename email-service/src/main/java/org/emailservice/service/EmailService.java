package org.emailservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.emailservice.dto.BookingConfirmationRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;
    public EmailService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    public void sendBookingConfirmation(BookingConfirmationRequest request){
        try {
            MimeMessage mineMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mineMessage, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(request.getToEmail());
            helper.setSubject("Booking Confirmation - " + request.getBookingId());
            helper.setText(buildBookingConfirmationHtml(request), true);
            mailSender.send(mineMessage);
        }catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String buildBookingConfirmationHtml(BookingConfirmationRequest request) {
        return """
        <div style="font-family: Arial, Helvetica, sans-serif; background-color: #f4f6f8; padding: 30px 0;">
          <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="max-width: 480px; margin: 0 auto; background: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.08);">
            <tr>
              <td style="background-color: #1a73e8; padding: 24px; text-align: center;">
                <img src="https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/72x72/2708.png"
                         alt="Airplane" width="60" height="60" style="display: block; margin: 0 auto 10px auto;" />
                <h1 style="color: #ffffff; font-size: 20px; margin: 0;">Booking Confirmed</h1>
              </td>
            </tr>
            <tr>
              <td style="padding: 30px;">
                <p style="font-size: 15px; color: #333333; margin: 0 0 20px 0;">
                  Great news! Your flight booking has been confirmed. Here are your details:
                </p>
                <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="border-collapse: collapse; margin-bottom: 20px;">
                  <tr>
                    <td style="padding: 10px 0; border-bottom: 1px solid #eeeeee; color: #888888; font-size: 13px;">Booking ID</td>
                    <td style="padding: 10px 0; border-bottom: 1px solid #eeeeee; color: #222222; font-size: 14px; font-weight: bold; text-align: right;">%s</td>
                  </tr>
                  <tr>
                    <td style="padding: 10px 0; border-bottom: 1px solid #eeeeee; color: #888888; font-size: 13px;">Flight Number</td>
                    <td style="padding: 10px 0; border-bottom: 1px solid #eeeeee; color: #222222; font-size: 14px; font-weight: bold; text-align: right;">%s</td>
                  </tr>
                  <tr>
                    <td style="padding: 10px 0; border-bottom: 1px solid #eeeeee; color: #888888; font-size: 13px;">Seat Class</td>
                    <td style="padding: 10px 0; border-bottom: 1px solid #eeeeee; color: #222222; font-size: 14px; font-weight: bold; text-align: right;">%s</td>
                  </tr>
                  <tr>
                    <td style="padding: 10px 0; border-bottom: 1px solid #eeeeee; color: #888888; font-size: 13px;">Booking Time</td>
                    <td style="padding: 10px 0; border-bottom: 1px solid #eeeeee; color: #222222; font-size: 14px; font-weight: bold; text-align: right;">%s</td>
                  </tr>
                  <tr>
                    <td style="padding: 10px 0; color: #888888; font-size: 13px;">Price</td>
                    <td style="padding: 10px 0; color: #222222; font-size: 14px; font-weight: bold; text-align: right;">$%s</td>
                  </tr>
                </table>
                <p style="font-size: 13px; color: #999999; margin: 0;">
                  Please keep this email for your records. Safe travels!
                </p>
              </td>
            </tr>
            <tr>
              <td style="background-color: #f4f6f8; padding: 16px; text-align: center;">
                <p style="font-size: 12px; color: #aaaaaa; margin: 0;">&copy; Flight Booking System</p>
              </td>
            </tr>
          </table>
        </div>
        """.formatted(
                request.getBookingId(),
                request.getFlightNumber(),
                request.getSeatClass(),
                request.getBookingTime(),
                request.getPriceAtBooking()
        );
    }
}
