package com.softwaremill.flowsdemo;

import com.softwaremill.jox.Channel;
import com.softwaremill.jox.Source;
import com.softwaremill.jox.flows.ByteChunk;
import com.softwaremill.jox.flows.Flow;
import com.softwaremill.jox.flows.Flows;
import com.softwaremill.jox.structured.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class DemoReportLinesProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(DemoReportLinesProvider.class);

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String HEADER_LINE = "day,product,amount\n";
    private static final String LINE_TEMPLATE = "%s,%s,%d\n";
    private static final List<String> PRODUCTS = List.of("Tea", "Coffee", "Cup", "Sugar", "Milk");
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    static Source<ByteChunk> produceReportLines(Scope scope, int numberOfRows) {
        return Flows.range(1, numberOfRows, 1)
                .map(DemoReportLinesProvider::reportLine)
                .prepend(Flows.fromValues(ByteChunk.fromArray(HEADER_LINE.getBytes(StandardCharsets.UTF_8))))
                .onDone(() -> LOGGER.info("Produced all report lines"))
                .runToChannel(scope);
    }

    private static ByteChunk reportLine(Integer i) {
        var day = Instant.now()
                .minus(i / PRODUCTS.size(), ChronoUnit.DAYS) // make sure all products are present for one day
                .atZone(ZoneId.systemDefault());
        String product = PRODUCTS.get(i % PRODUCTS.size()); // get products in round-robin
        int amount = RANDOM.nextInt(100);

        String line = LINE_TEMPLATE.formatted(DATE_TIME_FORMATTER.format(day), product, amount);
        return ByteChunk.fromArray(line.getBytes(StandardCharsets.UTF_8));
    }
}
