package tests.examples;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import tests.BasicAssertion;

import static tests.BasicAssertion.*;


public class AssertionsTests {
    WebDriver wd;

    @BeforeEach
    void setup() {
        wd = new ChromeDriver();
        wd.get("https://the-internet.herokuapp.com/");
    }

    @Test
    void assertTest() {
        WebElement title = wd.findElement(By.xpath("//h1"));
        //assert title.getText().equals("Интернет");
        assertThat(title).textIsEqual("Интернет");
    }

    @Test
    void assertValueTest(){

        WebElement title = wd.findElement(By.xpath("//h1"));
        //assert title.getText().equals("Интернет");
        assertThat(title)
                .isVisible()
                .textIsEqual("Welcome to the-internet");

        WebElement inputButton = wd.findElement(By.xpath("//a[@href='/inputs']"));
        inputButton.click();
        WebElement inputField = wd.findElement(By.xpath("//input"));
        inputField.sendKeys("10");

        assertThat(inputField)
                .isVisible()
                .isSelected()
                .valueIsEqual("11");
    }

    @AfterEach
    void tearDown() {
        wd.quit();
    }

}
