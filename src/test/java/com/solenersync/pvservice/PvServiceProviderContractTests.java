package com.solenersync.pvservice;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.*;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
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
@PactBroker(url = "https://solenersync.pactflow.io")
//@PactFolder("pacts")
@IgnoreNoPactsToVerify
@IgnoreMissingStateChange
@ExtendWith(SpringExtension.class)
@ActiveProfiles("pact-provider")
public class PvServiceProviderContractTests {

	@MockBean
	RestTemplate restTemplate;

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

	@State("should return a solar forecast")
	void getUserByEmail() throws IOException {
		StubSetup.stubForGetHourlyPv(restTemplate);
	}
}

