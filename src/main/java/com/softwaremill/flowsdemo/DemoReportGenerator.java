package com.softwaremill.flowsdemo;

import com.softwaremill.jox.flows.ByteChunk;
import com.softwaremill.jox.flows.Flow;
import com.softwaremill.jox.flows.Flows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class DemoReportGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(DemoReportGenerator.class);

    private static final int NUMBER_OF_ROWS = 1_000;

    void generateReport(OutputStream outputStream) throws Exception {
        Path tmpReportPath = Files.createTempFile("report", UUID.randomUUID().toString());

        ScopedValue.callWhere(Flow.CHANNEL_BUFFER_SIZE, 36, () -> {
            Flow<ByteChunk> reportLines = DemoReportLinesProvider.produceReportLines(NUMBER_OF_ROWS);
            runReportLinesToTmpFile(reportLines, tmpReportPath);
            runContentFromTmpFileToOutputStream(outputStream, tmpReportPath);
            return null;
        });
    }

    private static void runReportLinesToTmpFile(Flow<ByteChunk> reportLines, Path tmpReportPath) throws Exception {
        try {
            reportLines
                    .toByteFlow()
                    .runToFile(tmpReportPath);
        } catch (Exception e) {
            LOGGER.error("Error while processing! %s\n", e);
            Files.deleteIfExists(tmpReportPath);
            throw e;
        }
    }

    private static void runContentFromTmpFileToOutputStream(OutputStream outputStream, Path tmpReportPath) throws Exception {
        try {
            Flows.fromFile(tmpReportPath, 128)
                    .runToOutputStream(outputStream);
        } catch (Exception e) {
            LOGGER.error("Exception while writing from file to response!");
            throw e;
        } finally {
            Files.deleteIfExists(tmpReportPath);
            LOGGER.info("Deleted tmp file!");
        }
    }
}
