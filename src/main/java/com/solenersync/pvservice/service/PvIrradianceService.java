package com.solenersync.pvservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solenersync.pvservice.model.Mounting;
import com.solenersync.pvservice.model.PvDetails;
import com.solenersync.pvservice.model.SolarArrayRequest;
import jakarta.websocket.DeploymentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PvIrradianceService {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private static final String PVGIS_URL =
        "https://re.jrc.ec.europa.eu/api/DRcalc?angle={angle}&aspect={aspect}&lat={lat}&lon={lon}&loss={loss}&" +
            "peakpower={peakPower}&mountingplace={mountingplace}&month={month}&global=1&clearsky=1&outputformat=json";

    @Autowired
    public PvIrradianceService(ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    public Optional<List<PvDetails>> getPvIrradiance(SolarArrayRequest request) throws DeploymentException {
        float angle = request.getAngle();
        float aspect = request.getAspect();
        float lat = request.getLat();
        float lon = request.getLon();
        float loss = request.getLoss();
        float peakPower = request.getPeakPower();
        int month = request.getMonth();
        Mounting mountingplace = request.getMounting();
        URI url = new UriTemplate(PVGIS_URL)
            .expand(angle, aspect, lat, lon, loss, peakPower, mountingplace, month);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return Optional.of(convert(response, peakPower));
    }

    private List<PvDetails> convert(ResponseEntity<String> response, float peakPower) {
        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            String jsonString = root.path("outputs").path("daily_profile").toString();
            log.debug("Returning {}", jsonString);
            PvDetails[] pvDetailsArray = objectMapper.readValue(jsonString, PvDetails[].class);
            for (PvDetails pvDetails : pvDetailsArray) {
                float globalPeak = peakPower * pvDetails.getGlobalIrradiance() / 1000;
                pvDetails.setPeakGlobalOutput(globalPeak);
            }
            return Arrays.asList(pvDetailsArray);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
