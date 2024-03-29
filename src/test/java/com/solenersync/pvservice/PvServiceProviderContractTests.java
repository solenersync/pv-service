package com.solenersync.pvservice;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.*;
import au.com.dius.pact.provider.junitsupport.loader.*;
import com.solenersync.pvservice.service.WeatherService;
import jakarta.websocket.DeploymentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Provider("pv-service")
@Consumer("ses-front-end")
@PactBroker(url = "https://solenersync.pactflow.io", authentication = @PactBrokerAuth(token = "${PACT_BROKER_TOKEN}"))
//@PactFolder("pacts")
@IgnoreMissingStateChange
@ExtendWith(SpringExtension.class)
@ActiveProfiles("pact-provider")
public class PvServiceProviderContractTests {


	@MockBean
	RestTemplate restTemplate;

	@MockBean
	WeatherService weatherService;

	@LocalServerPort
	private int port;

	@BeforeEach
	void setup(PactVerificationContext context) {
		if (context != null) {
			context.setTarget(new HttpTestTarget("localhost", port));
		}
	}

	@TestTemplate
	@ExtendWith(PactVerificationInvocationContextProvider.class)
	void pactVerificationTestTemplate(PactVerificationContext context) {
		if (context != null) {
			context.verifyInteraction();
		}
	}

	@State("a solar forecast is available")
	void getSolarForecast() throws IOException, DeploymentException {
		StubSetup.stubForGetHourlyPv(restTemplate, weatherService);
	}

	@State("a solar forecast is not available")
	void getSolarForecastFail() throws IOException {
		StubSetup.stubForGetHourlyPvFail(restTemplate);
	}
}

