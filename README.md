# sendgrid-email-service
Implement a Spring Boot REST API which allows sending an email to one or more recipients.
Email delivery should be done via sendgrid.com.
The API key is registred to this From email address:sendgrid@rakenapp.com.
I used Maven.

Example payload:
```json
{
    "to": ["test@gmail.com"],
    "subject": "Testing Email Service",
    "body": "This is a test email sent from my application.",
    "cc": [],
    "bcc": []
}
```

Added unit tests for the EmailServiceSendGrid class.
Added toggle in application.properties (email.filter.enabled=true)
Added an optional boolean QueryParam enrich=true to enrich the email messages 
by appending a random quote of the day from Ninja API (https://api-ninjas.com/api/quotes)