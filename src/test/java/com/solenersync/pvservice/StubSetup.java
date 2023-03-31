package com.solenersync.pvservice;

import com.solenersync.pvservice.model.WeatherDetails;
import com.solenersync.pvservice.service.WeatherService;
import jakarta.websocket.DeploymentException;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.swing.text.html.Option;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.codec.CharEncoding.UTF_8;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@UtilityClass
public class StubSetup {


    public void stubForGetHourlyPv(RestTemplate restTemplate, WeatherService weatherService) throws IOException, DeploymentException {

        List<WeatherDetails> weatherDetailsList = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            WeatherDetails weatherDetails = WeatherDetails.builder()
                .highCloud(1)
                .lowCloud(1)
                .midCloud(1)
                .maxCloudCover(1)
                .date("2023-04-02T00:00")
                .time("06:00")
                .build();
            weatherDetailsList.add(weatherDetails);
        }

        String body = FileUtils.readFileToString(new File("src/test/resources/irradiance-response.json"), UTF_8);
        ResponseEntity<String> resp = new ResponseEntity(body, new HttpHeaders(), HttpStatus.OK);

        String weatherBody = FileUtils.readFileToString(new File("src/test/resources/weatherResponse.json"), UTF_8);
        ResponseEntity<String> weatherResp = new ResponseEntity(weatherBody, new HttpHeaders(), HttpStatus.OK);

        when(restTemplate.getForEntity(any(), any())).thenAnswer(invocation -> resp);
        when(weatherService.getWeather(anyFloat(), anyFloat())).thenReturn(Optional.of(weatherDetailsList));

    }

    public void stubForGetHourlyPvFail(RestTemplate restTemplate) throws IOException {
        when(restTemplate.getForEntity(any(), any()))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad request"));
    }
}
