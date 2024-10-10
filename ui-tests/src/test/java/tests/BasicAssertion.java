package tests;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.WebElement;

public class BasicAssertion extends AbstractAssert<BasicAssertion, WebElement> {
    private BasicAssertion(WebElement actual) {
        super(actual, AbstractAssert.class);
    }

    public static BasicAssertion assertThat(WebElement actual) {
        return new BasicAssertion(actual);
    }

    public BasicAssertion textIsEqual(String expectedText) {
        Assertions.assertThat(actual.getText())
                .as("Текст элемента не равен '%s'", expectedText)
                .isEqualTo(expectedText);

        return this;
    }

    public BasicAssertion valueIsEqual(String expectedValue) {
        Assertions.assertThat(actual.getAttribute("value"))
                .as("Значение элемента не равно '%s'", expectedValue)
                .isEqualTo(expectedValue);

        return this;
    }

    public BasicAssertion isVisible(){
        Assertions.assertThat(actual.isDisplayed())
                .as("Объект не виден!")
                .isTrue();

        return this;
    }
    public BasicAssertion isSelected(){
        Assertions.assertThat(actual.isSelected())
                .as("Объект не выбран!")
                .isTrue();

        return this;
    }

}
