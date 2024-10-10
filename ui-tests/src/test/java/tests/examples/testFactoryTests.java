package tests.examples;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.List;

public class testFactoryTests {
    @TestFactory
    List<DynamicTest> testFactoryTestExample() {
        List<DynamicTest> tests = new ArrayList<DynamicTest>();
        tests.add(DynamicTest.dynamicTest("String1",
                ()-> System.out.println("Test from factory 1")));

        tests.add(DynamicTest.dynamicTest("String2",
                ()-> System.out.println("Test from factory 2")));

        tests.add(DynamicTest.dynamicTest("String3",
                ()-> System.out.println("Test from factory 3")));

        return tests;
    }
}
