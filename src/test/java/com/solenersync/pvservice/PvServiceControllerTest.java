package com.solenersync.pvservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solenersync.pvservice.controller.PvServiceController;
import com.solenersync.pvservice.model.Mounting;
import com.solenersync.pvservice.model.PvDetails;
import com.solenersync.pvservice.model.SolarArrayRequest;
import com.solenersync.pvservice.service.PvIrradianceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import net.joshka.junit.json.params.JsonFileSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import javax.json.JsonObject;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({MockitoExtension.class})
class PvServiceControllerTest {

	private MockMvc mockMvc;

	ObjectMapper objectMapper = new ObjectMapper();
	RestTemplate restTemplate = new RestTemplate();

	PvIrradianceService pvIrradianceService = new PvIrradianceService(objectMapper, restTemplate);

	@BeforeEach
	public void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(new PvServiceController()).build();
	}

	@ParameterizedTest
	@JsonFileSource(resources = "/pvgis-response.json")
	public void getHourlyPv(JsonObject json) throws Exception {
		List<PvDetails> pvDetailsList = List.of(PvDetails.builder()
			.month(1)
			.time("12:00")
			.diffuseIrradiance(1.1f)
			.directIrradiance(1.1f)
			.globalIrradiance(1.1f)
			.clearSkyIrradiance(1.1f)
			.build());

		ObjectMapper objectMapper = new ObjectMapper();

		SolarArrayRequest solarArrayRequest = SolarArrayRequest.builder().angle(2.2f).aspect(2.2f).lat(2.2f).lon(2.2f)
			.month(1).peakPower(2.2f).loss(2.2f).mounting(Mounting.FREE).build();
		String requestAsJson = objectMapper.writeValueAsString(solarArrayRequest);

//		when(restTemplate.getForEntity(anyString(), String.class)).thenReturn((ResponseEntity<String>) json);
//		mockMvc.perform(post("/v1/pv/daily")
//			.contentType(APPLICATION_JSON)
//			.content(requestAsJson))
//			.andExpect(status().isOk())
//			.andExpect(MockMvcResultMatchers.content().json(json.toString()));
	}

}
