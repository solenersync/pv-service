package com.solenersync.sesdemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SesDemoControllerIT {

	@Autowired
	private TestRestTemplate template;

	@Test
	public void getHello() throws Exception {
		ResponseEntity<String> response = template.getForEntity("/v1/ses-demo/test", String.class);
		assertThat(response.getBody()).isEqualTo("Testing 1...2...");
	}
}
