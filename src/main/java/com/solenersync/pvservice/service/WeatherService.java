package com.solenersync.pvservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solenersync.pvservice.model.*;
import jakarta.websocket.DeploymentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class WeatherService {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private static final String METEO_URL =
        "https://api.open-meteo.com/v1/forecast?latitude={lat}&longitude={lon}&hourly=cloudcover,cloudcover_low,cloudcover_mid,cloudcover_high";

    @Autowired
    public WeatherService(ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    public Optional<List<WeatherDetails>> getWeather(float lat, float lon) throws DeploymentException {
        URI url = new UriTemplate(METEO_URL)
            .expand(lat, lon);
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return Optional.of(convert(response));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private List<WeatherDetails> convert(ResponseEntity<String> response) {
        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode hourly = root.path("hourly");

            JsonNode cloudCoverLowArray = hourly.path("cloudcover_low");
            JsonNode cloudCoverMidArray = hourly.path("cloudcover_mid");
            JsonNode cloudCoverHighArray = hourly.path("cloudcover_high");
            JsonNode timeArray = hourly.path("time");

            int arrayLength = cloudCoverLowArray.size();
            WeatherDetails[] weatherDetailsArray = new WeatherDetails[arrayLength];

            for (int i = 0; i < arrayLength; i++) {
                WeatherDetails weatherDetails = new WeatherDetails();
                weatherDetails.setLowCloud(cloudCoverLowArray.get(i).asInt());
                weatherDetails.setMidCloud(cloudCoverMidArray.get(i).asInt());
                weatherDetails.setHighCloud(cloudCoverHighArray.get(i).asInt());
                LocalDateTime dateTime = LocalDateTime.parse(timeArray.get(i).asText());
                weatherDetails.setDate(String.valueOf(dateTime));
                weatherDetails.setTime(dateTime.toLocalTime().toString());
                int maxCloudCover = getLarger(weatherDetails.getLowCloud(), weatherDetails.getMidCloud());
                weatherDetails.setMaxCloudCover(maxCloudCover);
                weatherDetailsArray[i] = weatherDetails;
            }
            return Arrays.asList(weatherDetailsArray);
        } catch (JsonProcessingException e) {
            log.debug("error converting response");
        }
        return null;
    }


    private static int getLarger(int a, int b) {
        if (a > b) {
            return a;
        } else {
            return b;
        }
    }
}
