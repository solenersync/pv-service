package com.solenersync.pvservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solenersync.pvservice.model.Mounting;
import com.solenersync.pvservice.model.PvForecastDetails;
import com.solenersync.pvservice.model.SolarArrayRequest;
import com.solenersync.pvservice.model.WeatherDetails;
import com.solenersync.pvservice.service.PvIrradianceService;
import com.solenersync.pvservice.service.WeatherService;
import jakarta.websocket.DeploymentException;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.codec.CharEncoding.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class PvIrradianceServiceUnitTests {

	@Mock
	RestTemplate restTemplate;

	@Mock
	WeatherService weatherService;

	ObjectMapper objectMapper = new ObjectMapper();
	PvIrradianceService pvIrradianceService;
	ObjectMapper mapper = new ObjectMapper();
	String body;


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
		pvIrradianceService = new PvIrradianceService(objectMapper, restTemplate, weatherService);}

	@Test
	void should_return_list_of_pv_details_with_valid_request() throws IOException, DeploymentException {

		List<WeatherDetails> weatherDetailsList = new ArrayList<>();
		for (int i = 0; i < 24; i++) {
			WeatherDetails weatherDetails = WeatherDetails.builder()
				.highCloud(1)
				.lowCloud(1)
				.midCloud(1)
				.maxCloudCover(1)
				.date("2023-04-04T00:00")
				.time("06:00")
				.build();
			weatherDetailsList.add(weatherDetails);
		}


		body = FileUtils.readFileToString(new File("src/test/resources/irradiance-response.json"), UTF_8);
		ResponseEntity<String> resp = new ResponseEntity(body, new HttpHeaders(), HttpStatus.OK);

		when(restTemplate.getForEntity(any(), any())).thenAnswer(invocation -> resp);
		when(weatherService.getWeather(anyFloat(),anyFloat())).thenAnswer(invocation -> Optional.of(weatherDetailsList));

		Optional<List<PvForecastDetails>> actualResult = pvIrradianceService.getPvIrradiance(solarArrayRequest);

		List<PvForecastDetails> extractList = actualResult.orElseThrow(IllegalArgumentException::new);
		assertThat(extractList).hasSizeGreaterThan(1);
		assertThat(extractList).hasAtLeastOneElementOfType(PvForecastDetails.class);
	}

	@Test
	void should_throw_exception_when_optional_is_empty() throws DeploymentException {
		when(restTemplate.getForEntity(any(), any())).thenThrow(new RuntimeException("error"));
		Optional<List<PvForecastDetails>> actualResult = pvIrradianceService.getPvIrradiance(solarArrayRequest);
		assertThrows(IllegalArgumentException.class, () -> actualResult.orElseThrow(IllegalArgumentException::new));
	}

}
