package com.solenersync.pvservice.service;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solenersync.pvservice.model.Mounting;
import com.solenersync.pvservice.model.PvDetails;
import com.solenersync.pvservice.model.SolarArrayRequest;
import jakarta.websocket.DeploymentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Service
public class PvIrradianceService {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private static final String PVGIS_URL =
        "https://re.jrc.ec.europa.eu/api/DRcalc?angle={angle}&aspect={aspect}&lat={lat}&lon={lon}&loss={loss}&" +
            "peakpower={peakpower}&mountingplace={mountingplace}&month={month}&global=1&clearsky=1&outputformat=json";

    @Autowired
    public PvIrradianceService(ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    public List<PvDetails> getPvIrradiance(SolarArrayRequest request) throws DeploymentException {
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
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);;
        return convert(response);
    }

    private List<PvDetails> convert(ResponseEntity<String> response) {
        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            String jsonString = root.path("outputs").path("daily_profile").toString();
            return Arrays.asList(objectMapper.readValue(jsonString, PvDetails[].class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
