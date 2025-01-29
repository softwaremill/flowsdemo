package com.softwaremill.flowsdemo;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
public class ReportGenerationController {

    @GetMapping("/report")
    public ResponseEntity<StreamingResponseBody> downloadFile() {
        var reportGenerator = new DemoReportGenerator();

        StreamingResponseBody stream = outputStream -> {
            try {
                reportGenerator.generateReport(outputStream);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        ContentDisposition contentDisposition = ContentDisposition.builder("attachment").filename("file.csv").build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/csv")
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(stream);
    }

}
