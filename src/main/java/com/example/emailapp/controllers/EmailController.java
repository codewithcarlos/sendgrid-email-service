package com.example.emailapp.controllers;

import com.example.emailapp.services.EmailServiceSendGrid;
import com.example.emailapp.services.dtos.EmailDTO;
import com.sendgrid.helpers.mail.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/email")
public class EmailController {

    private final EmailServiceSendGrid emailServiceSendGrid;

    @Autowired
    public EmailController(EmailServiceSendGrid emailServiceSendGrid) {
        this.emailServiceSendGrid = emailServiceSendGrid;
    }

    @PostMapping("/send")
    public Mail sendEmail(@RequestParam(required = false, defaultValue = "false") boolean enrich, @RequestBody EmailDTO emailDTO) throws IOException {
        return emailServiceSendGrid.sendEmail(emailDTO, enrich);
    }
}

