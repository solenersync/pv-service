package com.solenersync.pvservice;

import lombok.experimental.UtilityClass;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.codec.CharEncoding.UTF_8;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@UtilityClass
public class StubSetup {

    public void stubForGetHourlyPv(RestTemplate restTemplate) throws IOException {
        String body = FileUtils.readFileToString(new File("src/test/resources/irradiance-response.json"), UTF_8);
        ResponseEntity<String> resp = new ResponseEntity(body, new HttpHeaders(), HttpStatus.OK);
        when(restTemplate.getForEntity(any(), any())).thenAnswer(invocation -> resp);
    }

    public void stubForGetHourlyPvFail(RestTemplate restTemplate) throws IOException {
        when(restTemplate.getForEntity(any(), any()))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad request"));
    }
}
