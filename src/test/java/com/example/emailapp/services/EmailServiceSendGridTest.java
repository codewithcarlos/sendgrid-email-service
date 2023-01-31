package com.example.emailapp.services;

import com.example.emailapp.services.dtos.EmailDTO;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"email.filter.enabled=true"})
class EmailServiceSendGridTest {
    @Autowired
    private EmailServiceSendGrid emailServiceSendGrid;

    EmailServiceSendGrid spyEmailService = spy(new EmailServiceSendGrid());

    @Mock
    private SendGrid sendGrid;

    @Test
    public void testBuildEmail() throws IOException {
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setTo(List.of("sendgrid@rakenapp.com"));
        emailDTO.setSubject("Test Subject");
        emailDTO.setBody("Test Body");
        emailDTO.setCc(List.of());
        emailDTO.setBcc(List.of());

        Mail mail = emailServiceSendGrid.buildEmail(emailDTO, false);
        assertNotNull(mail);
        assertEquals("Test Subject", mail.getSubject());
        assertEquals("test@gmail.com", mail.getFrom().getEmail());
        assertEquals("Example User", mail.getFrom().getName());
        assertEquals(1, mail.getPersonalization().size());
        assertEquals("sendgrid@rakenapp.com", mail.getPersonalization().get(0).getTos().get(0).getEmail());
        assertEquals("Test Body", mail.getContent().get(0).getValue());
    }

    @Test
    public void testBuildEmails() throws IOException {
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setTo(List.of("test1@test.com", "test2@test.com"));
        emailDTO.setSubject("Test Subject");
        emailDTO.setBody("Test Body");
        emailDTO.setCc(List.of("test3@test.com", "test4@test.com"));
        emailDTO.setBcc(List.of("test5@test.com", "test6@test.com", "test7@test.com"));

        Mail mail = emailServiceSendGrid.buildEmail(emailDTO, false);
        assertNotNull(mail);
        assertEquals("Test Subject", mail.getSubject());
        assertEquals("test@gmail.com", mail.getFrom().getEmail());
        assertEquals("Example User", mail.getFrom().getName());
        assertEquals("Test Body", mail.getContent().get(0).getValue());

        assertEquals(1, mail.getPersonalization().size());
        assertEquals("test1@test.com", mail.getPersonalization().get(0).getTos().get(0).getEmail());
        assertEquals("test2@test.com", mail.getPersonalization().get(0).getTos().get(1).getEmail());
        assertEquals("test3@test.com", mail.getPersonalization().get(0).getCcs().get(0).getEmail());
        assertEquals(2, mail.getPersonalization().get(0).getCcs().size());
        assertEquals(3, mail.getPersonalization().get(0).getBccs().size());
        assertEquals("test4@test.com", mail.getPersonalization().get(0).getCcs().get(1).getEmail());
        assertEquals("test5@test.com", mail.getPersonalization().get(0).getBccs().get(0).getEmail());
        assertEquals("test6@test.com", mail.getPersonalization().get(0).getBccs().get(1).getEmail());
        assertEquals("test7@test.com", mail.getPersonalization().get(0).getBccs().get(2).getEmail());
    }

    @Test
    public void testSendEmail() throws IOException {
        boolean enrich = true;
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setTo(List.of("test1@rakenapp.com", "test2@test.com"));
        emailDTO.setSubject("Test Subject");
        emailDTO.setBody("Test Body");
        emailDTO.setCc(List.of("test3@test.com", "test4@test.com"));
        emailDTO.setBcc(List.of("test5@test.com", "test6@test.com", "test7@test.com"));
        List<String> filteredEmails = new ArrayList<>(List.of("test1@rakenapp.com"));
        doReturn(filteredEmails).when(spyEmailService).getFilteredEmails(emailDTO);
        Mail mail = new Mail();
        doReturn(mail).when(spyEmailService).buildEmail(emailDTO, enrich);
        doNothing().when(spyEmailService).send(mail);

        spyEmailService.sendEmail(emailDTO, enrich);
        verify(spyEmailService).getFilteredEmails(emailDTO);
        verify(spyEmailService).buildEmail(emailDTO, enrich);
        verify(spyEmailService).send(mail);

    }


    @Test
    void testGetFilteredEmails_withRakenAppEmails() {
        List<String> emailAddresses = new ArrayList<>();
        emailAddresses.add("user1@rakenapp.com");
        emailAddresses.add("user2@rakenapp.com");

        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setTo(emailAddresses);
        List<String> filteredEmails = emailServiceSendGrid.getFilteredEmails(emailDTO);

        assertEquals(emailAddresses, filteredEmails);
    }

    @Test
    void testGetFilteredEmails_withNonRakenAppEmails() {
        List<String> emailAddresses = new ArrayList<>();
        emailAddresses.add("user1@example.com");
        emailAddresses.add("user2@example.com");

        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setTo(emailAddresses);
        List<String> filteredEmails = emailServiceSendGrid.getFilteredEmails(emailDTO);

        assertTrue(filteredEmails.isEmpty());
    }

    @Test
    void testGetFilteredEmails_withMixedEmails() {
        List<String> emailAddresses = new ArrayList<>();
        emailAddresses.add("user1@example.com");
        emailAddresses.add("user2@rakenapp.com");

        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setTo(emailAddresses);

        List<String> filteredEmails = emailServiceSendGrid.getFilteredEmails(emailDTO);

        assertEquals(1, filteredEmails.size());
        assertTrue(filteredEmails.contains("user2@rakenapp.com"));
    }

    @Test
    public void sendTest() throws IOException {
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setTo(List.of("test1@test.com", "test2@test.com"));
        emailDTO.setSubject("Test Subject");
        emailDTO.setBody("Test Body");
        emailDTO.setCc(List.of("test3@test.com", "test4@test.com"));
        emailDTO.setBcc(List.of("test5@test.com", "test6@test.com", "test7@test.com"));

        Mail mail = emailServiceSendGrid.buildEmail(emailDTO, true);

        emailServiceSendGrid.send(mail);
    }
}