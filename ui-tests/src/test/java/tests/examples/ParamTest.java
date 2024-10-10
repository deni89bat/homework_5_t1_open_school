package tests.examples;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class ParamTest {
    @ParameterizedTest
    @ValueSource(strings = {"String1", "String2"})
    void paramTest(String param) {
        System.out.println(param);
    }

    @ParameterizedTest
    @EnumSource(value = ENUM.class)
    public void paramEnumsTest(ENUM param) {
        System.out.println(param);
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    public void paramMethodSourceTest(String param) {
        System.out.println(param);
    }

    public static String[] getTestData() {
        return new String[]{"String1", "String2"};
    }

    @RepeatedTest(failureThreshold = 12, value =50,  name = "Попытка {currentRepetition} из {totalRepetitions}" )
    @MethodSource("getTestData")
    public void repeatedExmpleTest(RepetitionInfo info) {
        System.out.println(info.getCurrentRepetition());
        assert 1==2;
    }
}
