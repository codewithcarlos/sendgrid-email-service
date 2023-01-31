package com.example.emailapp.services.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.List;

@Getter
@Setter
public class EmailDTO {

    @Email
    @NotNull
    @Size(min = 1, max = 254)
    private List<String> to;

    // RFC 2822 recommends 78 character limit
    @NotBlank
    @Size(max = 78)
    private String subject;

    @Pattern(regexp="^(<[^>]+>.*<\\/[^>]+>|[^<>]+)$")
    private String body;

    @Email
    @NotNull
    @Size(min = 1, max = 254)
    private List<String> cc;

    @Email
    @NotNull
    @Size(min = 1, max = 254)
    private List<String> bcc;
}

