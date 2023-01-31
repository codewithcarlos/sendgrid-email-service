package com.example.emailapp.services;

import com.example.emailapp.services.dtos.EmailDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
public class EmailServiceSendGrid implements EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceSendGrid.class);

    @Value("${email.filter.enabled}")
    private Boolean emailFilterEnabled;


    public Mail buildEmail(EmailDTO emailDTO, boolean enrich) throws IOException {
        List<String> toEmails = emailDTO.getTo();
        if (toEmails.isEmpty()) {
            return null;
        }
        Mail mail = new Mail();

        Email fromEmail = new Email();
        fromEmail.setName("Example User");
        fromEmail.setEmail("sendgrid@rakenapp.com");
        mail.setFrom(fromEmail);

        mail.setSubject(emailDTO.getSubject());

        Personalization personalization = new Personalization();
        for (String to : emailDTO.getTo()) {
            personalization.addTo(new Email(to));
        }
        for (String bcc : emailDTO.getBcc()) {
            personalization.addBcc(new Email(bcc));
        }
        for (String cc : emailDTO.getCc()) {
            personalization.addCc(new Email(cc));
        }
        mail.addPersonalization(personalization);

        Content content = new Content();
        content.setType("text/html");
        content.setValue(emailDTO.getBody());

        if (enrich) {
            try {
                JsonNode quote = QuoteService.getQuote().get(0).get("quote");
                content.setValue(content.getValue() + "\n" + quote);
            } catch (Exception e) {
                logger.warn("Unable to fetch random quote");
            }
        }
        mail.addContent(content);

        return mail;
    }

    @Override
    public Mail sendEmail(EmailDTO emailDTO) throws IOException {
        throw new UnsupportedOperationException("This method is not supported.");
    }

    @Override
    public Mail sendEmail(EmailDTO emailDTO, boolean enrich) throws IOException {
        emailDTO.setTo(getFilteredEmails(emailDTO));
        Mail mail = buildEmail(emailDTO, enrich);
        if (mail == null) {
            throw new IOException("No valid `to` emails were provided");
        }
        send(mail);
        return mail;
    }

    public List<String> getFilteredEmails(EmailDTO emailDTO) {
        List<String> emailAddresses = emailDTO.getTo();
        List<String> filteredEmails = new ArrayList<>();
        for (String emailAddress : emailAddresses) {
            if (emailFilterEnabled && !emailAddress.endsWith("rakenapp.com")) {
                logger.warn("Email to non-rakenapp.com domain filtered out: " + emailAddress);
            } else {
                filteredEmails.add(emailAddress);
            }
        }
        return filteredEmails;
    }

    public void send(final Mail mail) throws IOException {
        final SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));

        final Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        sg.api(request);
    }
}
