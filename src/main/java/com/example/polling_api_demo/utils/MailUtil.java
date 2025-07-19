package com.example.polling_api_demo.utils;

import com.example.polling_api_demo.entities.Poll;
import com.example.polling_api_demo.entities.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailUtil {

    private final JavaMailSender javaMailSender;

    @Value("${app.sender.email}")
    private String senderEmail;

    public void createPollMessage(User user, Poll createdPoll) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(senderEmail);
            mimeMessageHelper.setTo(user.getEmail());
            mimeMessageHelper.setSubject("New Poll Posted");
            mimeMessageHelper.setText("Dear " + user.getFirstName()
                    + ".\nThis message is to inform you that your poll has been successfully posted.\n"
                    + "The question you submitted is as follows: '"
                    + createdPoll.getQuestion() + "'.\nThis poll was posted on "
                    + createdPoll.getPostedDate() + ".\nYour poll is scheduled to expire on "
                    + createdPoll.getExpiredAt() + ".\nThank you for your contribution to this project!");
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            System.out.println("Failed to send email: " + e.getMessage());
        }
    }

    public void resetPasswordMessage(User user, String newPassword) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(senderEmail);
            mimeMessageHelper.setTo(user.getEmail());
            mimeMessageHelper.setSubject("Password Reset Request");
            mimeMessageHelper.setText("Your new password: " + newPassword);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            System.out.println("Failed to send email: " + e.getMessage());
        }
    }
}
