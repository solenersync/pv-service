package com.solenersync.pvservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solenersync.pvservice.model.Mounting;
import com.solenersync.pvservice.model.SolarArrayRequest;
import com.solenersync.pvservice.model.WeatherDetails;
import com.solenersync.pvservice.model.WeatherRequest;
import com.solenersync.pvservice.service.WeatherService;
import jakarta.websocket.DeploymentException;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.codec.CharEncoding.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class WeatherServiceUnitTests {

	@Mock
	RestTemplate restTemplate;

	WeatherService weatherService;
	final ObjectMapper objectMapper = new ObjectMapper();


	SolarArrayRequest solarArrayRequest = SolarArrayRequest.builder()
		.userId(12)
		.angle(35f)
		.aspect(2f)
		.lat(52.207306f)
		.lon(-6.52026f)
		.month(1)
		.peakPower(8.2f)
		.loss(0.145f)
		.mounting(Mounting.FREE)
		.date("2023-04-04T06:00")
		.build();

	@BeforeEach
	public void setUp() {
		weatherService = new WeatherService(objectMapper, restTemplate);
	}

	@Test
	void should_return_weather_details_for_valid_lat_lon() throws IOException, DeploymentException {
		String resp = FileUtils.readFileToString(new File("src/test/resources/weatherResponse.json"), UTF_8);
		ResponseEntity<String> response = new ResponseEntity<>(resp, HttpStatus.OK);
		when(restTemplate.getForEntity(any(), any(Class.class))).thenReturn(response);

		Optional<List<WeatherDetails>> result = weatherService.getWeather(52.207306f, -6.52026f);

		assertThat(result).isPresent();
		assertThat(result.get()).hasSizeGreaterThan(0);
		assertThat(result.get()).hasAtLeastOneElementOfType(WeatherDetails.class);
	}

	@Test
	void should_return_empty_optional_for_invalid_lat_lon() throws DeploymentException {
		when(restTemplate.getForEntity(any(), any())).thenThrow(RuntimeException.class);
		Optional<List<WeatherDetails>> result = weatherService.getWeather(0, 0);
		assertThat(result).isNotPresent();
	}

	@Test
	void should_set_and_get_a_weather_request() {
		float lat = 1.1f;
		float lon = -1.2f;
		WeatherRequest request = new WeatherRequest();
		request.setLat(lat);
		request.setLon(lon);
		assertThat(request.getLat()).isEqualTo(lat);
		assertThat(request.getLon()).isEqualTo(lon);
	}

	@Test
	void should_build_a_weather_request() {
		float lat = 1.1f;
		float lon = -1.2f;
		WeatherRequest request = WeatherRequest.builder()
			.lat(lat)
			.lon(lon)
			.build();
		assertThat(request.getLat()).isEqualTo(lat);
		assertThat(request.getLon()).isEqualTo(lon);
	}
}
