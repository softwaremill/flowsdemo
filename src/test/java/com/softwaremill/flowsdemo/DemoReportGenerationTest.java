package com.softwaremill.flowsdemo;

import com.softwaremill.jox.flows.Flows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoReportGenerationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldGenerateReport() throws Exception {
        //given
        String url = "http://localhost:%d/report".formatted(port);

        // when
        byte[] report = this.restTemplate.getForObject(url, byte[].class);

        // then
        List<String> lines = Flows.fromByteArrays(report)
                .linesUtf8()
                .filter(s -> !s.isBlank()) // filter out last empty line
                .runToList();

        assertThat(lines.getFirst()).isEqualTo("day,product,amount");
        assertThat(lines).hasSize(1_001);
    }
}

