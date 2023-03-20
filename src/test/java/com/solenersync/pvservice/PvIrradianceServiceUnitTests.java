package com.solenersync.pvservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solenersync.pvservice.model.Mounting;
import com.solenersync.pvservice.model.PvDetails;
import com.solenersync.pvservice.model.SolarArrayRequest;
import com.solenersync.pvservice.service.PvIrradianceService;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.codec.CharEncoding.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class PvIrradianceServiceUnitTests {

	@Mock
	RestTemplate restTemplate;

	ObjectMapper objectMapper = new ObjectMapper();
	PvIrradianceService pvIrradianceService;
	String body;

	PvDetails pvDetails = PvDetails.builder()
		.month(1)
		.time("00:00")
		.diffuseIrradiance(0.0f)
		.directIrradiance(0.0f)
		.globalIrradiance(0.0f)
		.clearSkyIrradiance(0.0f)
		.peakGlobalOutput(0.0f)
		.build();

	SolarArrayRequest solarArrayRequest = SolarArrayRequest.builder()
		.angle(35f)
		.aspect(2f)
		.lat(52.207306f)
		.lon(-6.52026f)
		.month(1)
		.peakpower(8.2f)
		.loss(0.145f)
		.mounting(Mounting.FREE)
		.build();

	@BeforeEach
	public void setUp() {
		pvIrradianceService = new PvIrradianceService(objectMapper, restTemplate);}

	@Test
	void should_return_list_of_pv_details_with_valid_request() throws IOException, DeploymentException {
		body = FileUtils.readFileToString(new File("src/test/resources/irradiance-response.json"), UTF_8);
		String pvDetailsJson = FileUtils.readFileToString(new File("src/test/resources/pv-details-list.json"), UTF_8);

		ObjectMapper mapper = new ObjectMapper();
		List<PvDetails> expectedList = Arrays.asList(mapper.readValue(pvDetailsJson, PvDetails[].class));
		System.out.println(expectedList);
		ResponseEntity<String> resp = new ResponseEntity(body, new HttpHeaders(), HttpStatus.OK);
		when(restTemplate.getForEntity(any(), any())).thenAnswer(invocation -> resp);
		Optional<List<PvDetails>> actualResult = pvIrradianceService.getPvIrradiance(solarArrayRequest);
		List<PvDetails> extractList = actualResult.orElseThrow(IllegalArgumentException::new);
		assertThat(extractList).hasSizeGreaterThan(1);
		assertThat(extractList).hasAtLeastOneElementOfType(PvDetails.class);
	}

}
