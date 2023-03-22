package com.solenersync.pvservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solenersync.pvservice.controller.PvServiceController;
import com.solenersync.pvservice.model.Mounting;
import com.solenersync.pvservice.model.PvDetails;
import com.solenersync.pvservice.model.SolarArrayRequest;
import com.solenersync.pvservice.service.PvIrradianceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith({MockitoExtension.class})
class PvServiceControllerUnitTests {

	private MockMvc mockMvc;

	@Mock
	PvIrradianceService pvIrradianceService;

	ObjectMapper objectMapper = new ObjectMapper();
//	RestTemplate restTemplate = new RestTemplate();

//	PvIrradianceService pvIrradianceService = new PvIrradianceService(objectMapper, restTemplate);

	@BeforeEach
	public void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(new PvServiceController(pvIrradianceService)).build();
	}

	@Test
	public void getHourlyPv() throws Exception {
		List<PvDetails> pvDetailsList = List.of(PvDetails.builder()
			.month(1)
			.time("12:00")
			.diffuseIrradiance(1.1f)
			.directIrradiance(1.1f)
			.globalIrradiance(1.1f)
			.clearSkyIrradiance(1.1f)
			.build());

		ObjectMapper objectMapper = new ObjectMapper();

		SolarArrayRequest solarArrayRequest = SolarArrayRequest.builder()
			.angle(2.2f)
			.aspect(2.2f)
			.lat(2.2f)
			.lon(2.2f)
			.month(1)
			.peakPower(2.2f)
			.loss(2.2f)
			.mounting(Mounting.FREE)
			.build();

//		when(pvIrradianceService.getPvIrradiance(solarArrayRequest)).thenReturn(Optional.of(pvDetailsList));
//				mockMvc.perform(post("/api/v1/pv/daily")
//			.contentType(APPLICATION_JSON)
//			.content(objectMapper.writeValueAsString(solarArrayRequest)))
//			.andExpect(status().isOk())
//			.andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(pvDetailsList)));
	}

		// Then
//		assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
//		assertTrue(responseEntity.getBody().isEmpty());
//		when(restTemplate.getForEntity(anyString(), any(Class.class))).thenReturn((ResponseEntity<String>) json);
//		mockMvc.perform(post("/api/v1/pv/daily")
//			.contentType(APPLICATION_JSON)
//			.content(objectMapper.writeValueAsString(solarArrayRequest)))
//			.andExpect(status().isOk())
//			.andExpect(MockMvcResultMatchers.content().json(json.toString()));
//	}

}
