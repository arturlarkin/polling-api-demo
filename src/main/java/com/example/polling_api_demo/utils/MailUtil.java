package com.example.polling_api_demo.utils;

import com.example.polling_api_demo.entities.Poll;
import com.example.polling_api_demo.entities.User;
import io.mailtrap.client.MailtrapClient;
import io.mailtrap.config.MailtrapConfig;
import io.mailtrap.factory.MailtrapClientFactory;
import io.mailtrap.model.request.emails.Address;
import io.mailtrap.model.request.emails.MailtrapMail;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MailUtil {
    @Value("${app.sender.email}")
    private String senderEmail;

    @Value("${app.mail.token}")
    private String token;

    public void createPollMessage(User user, Poll createdPoll) {
        final MailtrapConfig config = new MailtrapConfig.Builder()
                .token(token)
                .build();

        final MailtrapClient client = MailtrapClientFactory.createMailtrapClient(config);

        final MailtrapMail mail = MailtrapMail.builder()
                .from(new Address(senderEmail, "Polling App"))
                .to(List.of(new Address(user.getEmail())))
                .subject("New Poll Posted")
                .text("Dear " + user.getFirstName()
                        + ".\nThis message is to inform you that your poll has been successfully posted.\n"
                        + "The question you submitted is as follows: '"
                        + createdPoll.getQuestion() + "'.\nThis poll was posted on "
                        + createdPoll.getPostedDate() + ".\nYour poll is scheduled to expire on "
                        + createdPoll.getExpiredAt() + ".\nThank you for your contribution to this project!")
                .build();

        try {
            client.send(mail);
        } catch (Exception e) {
            System.out.println("Caught exception : " + e);
        }
    }

    public void resetPasswordMessage(User user, String newPassword) {
        final MailtrapConfig config = new MailtrapConfig.Builder()
                .token(token)
                .build();

        final MailtrapClient client = MailtrapClientFactory.createMailtrapClient(config);

        final MailtrapMail mail = MailtrapMail.builder()
                .from(new Address(senderEmail, "Polling App"))
                .to(List.of(new Address(user.getEmail())))
                .subject("Password Reset Request")
                .text("Your new password: " + newPassword)
                .build();

        try {
            client.send(mail);
        } catch (Exception e) {
            System.out.println("Caught exception : " + e);
        }
    }
}
