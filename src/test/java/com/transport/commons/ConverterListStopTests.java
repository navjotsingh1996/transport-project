package com.transport.commons;

import com.transport.services.invoicing.models.Stop;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ConverterListStopTests {

    private final ConverterListStop converterListStop = new ConverterListStop();

    private static Stop newStop(int index) {
        return new Stop(Instant.now().getEpochSecond(), "test" + index, "testCity" + index,
                "testState" + index, "testAddress" + index, index, index % 2 == 0 ? Stop.StopType.PICKUP : Stop.StopType.DELIVERY);
    }

    @Test
    void convertToAndFromDatabaseOkTests() {
        List<Stop> testStops = new ArrayList<>();
        testStops.add(newStop(1));
        testStops.add(newStop(2));
        testStops.add(newStop(3));
        assertThat(converterListStop.convertToEntityAttribute(converterListStop.convertToDatabaseColumn(testStops)))
                .isEqualTo(testStops);
    }

    @Test
    void convertToAndFromDatabaseFailTests() {
        // TODO: Mock static functions
    }
}
