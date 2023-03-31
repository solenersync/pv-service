package com.solenersync.pvservice;

import com.solenersync.pvservice.controller.PvServiceController;
import com.solenersync.pvservice.model.Mounting;
import com.solenersync.pvservice.model.PvForecastDetails;
import com.solenersync.pvservice.model.SolarArrayRequest;
import com.solenersync.pvservice.service.PvIrradianceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith({MockitoExtension.class})
class PvServiceControllerUnitTests {

	@InjectMocks
	private PvServiceController pvServiceController;

	@Mock
	private PvIrradianceService pvIrradianceService;

	private SolarArrayRequest solarArrayRequest;
	private List<PvForecastDetails> pvForecastDetailsList;

	@BeforeEach
	public void setUp() {
		pvForecastDetailsList = List.of(PvForecastDetails.builder()
			.time("12:00")
			.diffuseIrradiance(1.1f)
			.directIrradiance(1.1f)
			.globalIrradiance(1.1f)
			.clearSkyIrradiance(1.1f)
			.highCloud(1)
			.lowCloud(1)
			.midCloud(1)
			.maxCloudCover(1)
			.peakGlobalOutput(1)
			.totalPowerOutput(1)
			.date("date")
			.build());

		solarArrayRequest = SolarArrayRequest.builder()
			.angle(2.2f)
			.aspect(2.2f)
			.lat(2.2f)
			.lon(2.2f)
			.month(1)
			.peakPower(2.2f)
			.loss(2.2f)
			.mounting(Mounting.FREE)
			.month(1)
			.date("date")
			.build();
	}

	@Test
	public void should_return_pvForecast_data() throws Exception {

		when(pvIrradianceService.getPvIrradiance(any(SolarArrayRequest.class))).thenReturn(Optional.of(pvForecastDetailsList));
		ResponseEntity<List<PvForecastDetails>> response = pvServiceController.getHourlyPv(solarArrayRequest);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo(pvForecastDetailsList);
	}
}
