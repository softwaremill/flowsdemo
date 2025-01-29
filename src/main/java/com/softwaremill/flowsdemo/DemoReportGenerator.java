package com.softwaremill.flowsdemo;

import com.softwaremill.jox.Channel;
import com.softwaremill.jox.Source;
import com.softwaremill.jox.flows.ByteChunk;
import com.softwaremill.jox.flows.Flows;
import com.softwaremill.jox.structured.Scopes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class DemoReportGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(DemoReportGenerator.class);

    private static final int NUMBER_OF_ROWS = 1_000;

    void generateReport(OutputStream outputStream) throws Exception {
        Path tmpReportPath = Files.createTempFile("report", UUID.randomUUID().toString());

        ScopedValue.callWhere(Channel.BUFFER_SIZE, 36, () -> {
            Scopes.supervised(scope -> {
                Source<ByteChunk> channel = DemoReportLinesProvider.produceReportLines(scope, NUMBER_OF_ROWS);
                runReportLinesToTmpFile(channel, tmpReportPath);
                runContentFromTmpFileToOutputStream(outputStream, tmpReportPath);
                return null;
            });
            return null;
        });
    }

    private static void runReportLinesToTmpFile(Source<ByteChunk> channel, Path tmpReportPath) throws Exception {
        try {
            Flows.fromSource(channel)
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
                    .toByteFlow()
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
