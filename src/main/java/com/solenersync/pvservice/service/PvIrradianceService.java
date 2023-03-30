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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PvIrradianceService {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final WeatherService weatherService;
    private static final String PVGIS_URL =
        "https://re.jrc.ec.europa.eu/api/DRcalc?angle={angle}&aspect={aspect}&lat={lat}&lon={lon}&loss={loss}&" +
            "peakpower={peakpower}&mountingplace={mountingplace}&month={month}&global=1&clearsky=1&outputformat=json";

    @Autowired
    public PvIrradianceService(ObjectMapper objectMapper, RestTemplate restTemplate, WeatherService weatherService) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        this.weatherService = weatherService;
    }

    public Optional<List<PvForecastDetails>> getPvIrradiance(SolarArrayRequest request) throws DeploymentException {
        float angle = request.getAngle();
        float aspect = request.getAspect();
        float lat = request.getLat();
        float lon = request.getLon();
        float loss = request.getLoss();
        float peakpower = request.getPeakPower();
        int month = request.getMonth();
        Mounting mountingplace = request.getMounting();
        URI url = new UriTemplate(PVGIS_URL)
            .expand(angle, aspect, lat, lon, loss, peakpower, mountingplace, month);
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return Optional.of(convert(response, peakpower, lat, lon, request.getDate()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private List<PvForecastDetails> convert(ResponseEntity<String> response, float peakpower, float lat, float lon, String date) {
        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            String jsonString = root.path("outputs").path("daily_profile").toString();
            log.debug("Returning {}", jsonString);
            PvDetails[] pvDetailsArray = objectMapper.readValue(jsonString, PvDetails[].class);

            int arraySize = 24; // 7 days * 24 hrs
            PvForecastDetails [] pvForecastDetails = new PvForecastDetails[arraySize];
            for (int i = 0; i < pvForecastDetails.length; i++) {
                pvForecastDetails[i] = new PvForecastDetails();
            }

           WeatherDetails [] weatherArray = this.getForecast(lat, lon).orElse(Collections.emptyList()).toArray(new WeatherDetails[0]);
            WeatherDetails [] weatherArrayTrimmed = new WeatherDetails[arraySize];

            String formattedDate = date.substring(0, date.indexOf('T'));
            formattedDate = formattedDate + "T00:00";

            //copy into new array from where the date first occurs and the following 24 objects (24 hours)
            System.arraycopy(weatherArray, this.indexOfFirstMatchingDate(weatherArray, formattedDate), weatherArrayTrimmed, 0, arraySize);

            float totalPower = 0;
            for( int i = 0; i < pvDetailsArray.length; i++ ) {
                pvForecastDetails[i].setTime(pvDetailsArray[i].getTime());
                pvForecastDetails[i].setClearSkyIrradiance(pvDetailsArray[i].getClearSkyIrradiance());
                pvForecastDetails[i].setDiffuseIrradiance(pvDetailsArray[i].getDiffuseIrradiance());
                pvForecastDetails[i].setDirectIrradiance(pvDetailsArray[i].getDirectIrradiance());
                pvForecastDetails[i].setGlobalIrradiance(pvDetailsArray[i].getGlobalIrradiance());
                pvForecastDetails[i].setDate(weatherArrayTrimmed[i].getDate());
                pvForecastDetails[i].setLowCloud(weatherArrayTrimmed[i].getLowCloud());
                pvForecastDetails[i].setMidCloud(weatherArrayTrimmed[i].getMidCloud());
                pvForecastDetails[i].setHighCloud(weatherArrayTrimmed[i].getHighCloud());
                pvForecastDetails[i].setMaxCloudCover(weatherArrayTrimmed[i].getMaxCloudCover());

                float realOutput = (float) ((peakpower / 1000) * (pvForecastDetails[i].getGlobalIrradiance() * ( 1 - 0.65 * ( pvForecastDetails[i].getMaxCloudCover() / 100))));
                pvForecastDetails[i].setPeakGlobalOutput(realOutput);
                totalPower += realOutput;
                pvForecastDetails[i].setTotalPowerOutput(totalPower);
            }
            return Arrays.asList(pvForecastDetails);
        } catch (JsonProcessingException e) {
            log.debug("error converting response");
        } catch (DeploymentException e) {
            log.debug("deployment exception");
        }
        return null;
    }

    private Optional<List<WeatherDetails>> getForecast(float lat, float lon) throws DeploymentException {
        return weatherService.getWeather(lat, lon);
    }

    public int indexOfFirstMatchingDate(WeatherDetails [] weatherDetailsList, String date) {
        for (int i = 0; i < weatherDetailsList.length; i++) {
            if (weatherDetailsList[i].getDate().equals(date)) {
                return i;
            }
        }
        return -1;
    }

}
