package com.example.emailapp.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class QuoteService {
    public static JsonNode getQuote() throws IOException {
        URL url = new URL("https://api.api-ninjas.com/v1/quotes?category=happiness");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("accept", "application/json");
        connection.setRequestProperty("X-Api-Key", System.getenv("NINJA_API_KEY"));
        InputStream responseStream = connection.getInputStream();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(responseStream);
    }
}
