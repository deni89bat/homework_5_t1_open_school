package tests.examples;

import com.codeborne.selenide.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runners.Parameterized;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;

public class SelenideCollectionsTests {
/*    @BeforeEach
    void setup() {
        Configuration.browser = "chrome";
        Configuration.timeout = 2000;
        open("https://the-internet.herokuapp.com/");

    }*/

    @Test
    void selenideConditionsTest() {
        ElementsCollection links = $$x("//a[@href]");
        //links.should(CollectionCondition.size(1));
        SelenideElement element123 = $x("//asd");
        links.should(CollectionCondition.size(46));
        //links.should(CollectionCondition.allMatch("Не содержит нужный элемент NaN", element -> element.getText().equals("NaN")));
        //links.should(CollectionCondition.texts("Dropdown"));
        links.should(CollectionCondition.itemWithText("Dropdown"));

    }

    @ParameterizedTest
    @ValueSource(strings = {"String1", "String2"} )
    void paramTest(String param) {
        System.out.println(param);
    }

    @AfterEach
    void teardown() {
        closeWebDriver();
    }
}
