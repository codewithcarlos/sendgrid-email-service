package com.example.emailapp.services;

import com.example.emailapp.services.dtos.EmailDTO;
import com.sendgrid.helpers.mail.Mail;

import java.io.IOException;

public interface EmailService {
    public static Mail buildEmail(EmailDTO emailDTO) {
        return null;
    }

    public Mail sendEmail(EmailDTO emailDTO) throws IOException;
    public Mail sendEmail(EmailDTO emailDTO, boolean enrich) throws IOException;
}

