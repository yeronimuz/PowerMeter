package org.domiot.p1.pmagent;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

class RepeatValidatorTest {
    public static Stream<Arguments> provideTestValues() {
        return Stream.of(
                Arguments.of(LocalDateTime.parse("2024-04-18T12:30:59.980763"), true),
                Arguments.of(LocalDateTime.parse("2024-04-18T12:35:00.014142"), true),
                Arguments.of(LocalDateTime.parse("2024-04-18T12:30:57.980763"), false),
                Arguments.of(LocalDateTime.parse("2024-04-18T12:35:00.021142"), false),
                Arguments.of(LocalDateTime.parse("2024-04-18T12:35:01.314142"), false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestValues")
    void testShouldRepeatValueAfter(LocalDateTime timestamp, boolean expectedResult) {
        boolean result = RepeatValidator.isValueAroundMinuteBorder(timestamp);
        Assertions.assertEquals(expectedResult, result);
    }
}
