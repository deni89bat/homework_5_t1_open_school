package tests;

import com.codeborne.selenide.*;
import com.codeborne.selenide.junit5.ScreenShooterExtension;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.url;
import static io.qameta.allure.Allure.addAttachment;
import static org.junit.jupiter.api.Assertions.fail;

public class HomeWorkTests {
    TestSteps steps = new TestSteps();
    // Регистрация расширения для создания скриншотов
    @RegisterExtension
    static ScreenShooterExtension screenshotEmAll = new ScreenShooterExtension(true).to("target/screenshots");

    @BeforeAll
    public static void setup() {
        Configuration.browser = "chrome";
        Configuration.pageLoadStrategy = "eager";
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                .screenshots(true)  // Записывать скриншоты
                .savePageSource(true));
    }

    @BeforeEach
    void openBaseUrl() {
        open("https://the-internet.herokuapp.com/");
    }

    @ParameterizedTest
    @ValueSource(strings = {"ascOrder", "descOrder"})
    @DisplayName("Checkboxes")
    @Description("""
            Перейти на страницу Checkboxes. Выделить первый чекбокс, снять выделение со второго чекбокса. Вывести в консоль значение атрибута checked для каждого чекбокса.
            Проверять корректное состояние каждого чекбокса после каждого нажатия на него. Запустить тест с помощью @ParametrizedTest, изменяя порядок нажатия на чекбоксы с помощью одного параметра.""")
    public void checkboxesTest(String order) {

        SelenideElement checkboxesButton = $x("//a[@href='/checkboxes']");
        SelenideElement checkbox1 = $x("//form[@id='checkboxes']/input[1]");
        SelenideElement checkbox2 = $x("//form[@id='checkboxes']/input[2]");

        steps.clickLink(checkboxesButton, checkboxesButton.getText());

        if (Objects.equals(order, "ascOrder")) {
            steps.setCheckboxWithVerification(checkbox1);
            steps.setCheckboxWithVerification(checkbox2);
        } else {
            steps.setCheckboxWithVerification(checkbox2);
            steps.setCheckboxWithVerification(checkbox1);
        }

        steps.printCheckedAttribute(checkbox1, "checkbox 1");
        steps.printCheckedAttribute(checkbox2, "checkbox 2");
    }

    @Test
    @DisplayName("Dropdown")
    @Description("""
            Перейти на страницу Dropdown. Выбрать первую опцию, вывести в консоль текущий текст элемента dropdown, выбрать вторую опцию, вывести в консоль текущий текст элемента dropdown.
            Проверять корректное состояние каждого dropDown после каждого нажатия на него.""")
    public void dropdownTest() {
        SelenideElement dropdownButton = $x("//a[@href='/dropdown']");
        SelenideElement dropdownElement = $x("//select[@id='dropdown']");

        steps.clickLink(dropdownButton, dropdownButton.getText());
        steps.selectOption(dropdownElement, 1);
        steps.selectOption(dropdownElement, 2);
    }

    @RepeatedTest(value = 10, name = "Запуск {currentRepetition} из {totalRepetitions}")
    @DisplayName("Disappearing Elements")
    @Description("""
            Перейти на страницу Disappearing Elements. Добиться отображения 5 элементов, максимум за 10 попыток, если нет, провалить тест с ошибкой.
            Для каждого обновления страницы проверять наличие 5 элементов. Использовать @RepeatedTest""")
    public void disappearingElementsTest() {
        SelenideElement disappearingElementsButton = $x("//a[@href='/disappearing_elements']");
        steps.clickLink(disappearingElementsButton, disappearingElementsButton.getText());
        steps.check5Elements();
    }

    @TestFactory
    @DisplayName("Inputs")
    @Description("""
            Перейти на страницу Inputs. Ввести любое случайное число от 1 до 10 000. Вывести в консоль значение элемента Input.
            Добавить проверки в задание Inputs из предыдущей лекции. Проверить, что в поле ввода отображается именно то число, которое было введено.
            Повторить тест 10 раз, используя @TestFactory, с разными значениями, вводимыми в поле ввода.
            Создать проверку негативных кейсов (попытка ввести в поле латинские буквы, спецсимволы, пробел до и после числа).""")
    List<DynamicTest> inputsTestFactoryTest() {
        List<DynamicTest> tests = new ArrayList<>();
        SelenideElement inputsButton = $x("//a[@href='/inputs']");
        SelenideElement inputField = $x("//input");

        for (int i = 1; i <= 10; i++) {
            final int iteration = i;
            tests.add(DynamicTest.dynamicTest("Тест №" + iteration + ": Ввести любое случайное число от 1 до 10 000.",
                    () -> {
                        if (iteration == 1) {
                            steps.clickLink(inputsButton, inputsButton.getText());  // Кликаем на ссылку только в первом тесте
                        }
                        inputField.clear();  // Очищаем поле перед каждым вводом
                        steps.enterRandomNumberInInput(inputField);  // Вводим случайное число
                    }));
        }

        // Негативные кейсы: ввод букв, символов и пробелов
        List<String> negativeValues = List.of("abc",
                "#$%",
                " 123",
                "123 ",
                "123abc",
                "",
                "   ",
                "-123");

        for (String value : negativeValues) {
            tests.add(DynamicTest.dynamicTest("Негативный тест с вводом: '" + value + "'",
                    () -> {
                        inputField.clear();
                        inputField.sendKeys(value);
                        steps.checkInvalidInput(inputField, value);
                    }));
        }

        return tests;
    }

    @ParameterizedTest(name = "Проверка текста при наведении на картинку {0}")
    @CsvSource({
            "1, name: user1 View profile",
            "2, name: user2 View profile",
            "3, name: user3 View profile"
    })
    @DisplayName("Hover")
    @Description(""" 
            Перейти на страницу Hovers. Навести курсор на каждую картинку. Вывести в консоль текст, который появляется при наведении.
            Добавить проверки в задание Hovers из предыдущей лекции.
            При каждом наведении курсора, проверить, что отображаемый текст совпадает с ожидаемым.
            Выполнить тест с помощью @ParametrizedTest, в каждом тесте, указывая на какой элемент наводить курсор""")
    public void hoverTest(int imageIndex, String expectedText) {
        SelenideElement hoversButton = $x("//a[@href='/hovers']");
        steps.clickLink(hoversButton, hoversButton.getText());
        steps.hoverOnImageAndCheckText(imageIndex, expectedText);
    }

    @RepeatedTest(failureThreshold = 4, value = 10, name = "Запуск {currentRepetition} из {totalRepetitions}")
    @DisplayName("Notification Message")
    @Description("""
            Перейти на страницу Notification Message.
            После каждого неудачного клика закрывать всплывающее уведомление.
            Добавить проверки в задание Notification Message из предыдущей лекции.
            Добавить проверку, что всплывающее уведомление должно быть Successfull.
            Если нет – провалить тест.
            Использовать @RepeatedTest.""")
    public void notificationMessageRepeatedTest() {
        SelenideElement notificationMessageButton = $x("//a[@href='/notification_message']");
        steps.clickLink(notificationMessageButton, notificationMessageButton.getText());
        steps.checkNotification();
    }

    @TestFactory
    @DisplayName("Add/Remove Elements")
    @Description("""
            Перейти на страницу Add/Remove Elements.
            Создать 2/5/1 кнопки Delete.
            С каждым нажатием выводить в консоль текст появившегося элемента.
            Удалить 1/2/3 кнопки Delete.
            Выводить в консоль оставшееся количество кнопок Delete и их тексты.
            Добавить проверки в задание Add/Remove Elements.
            Проверять, что на каждом шагу остается видимым ожидаемое количество элементов.
            Запустить тест три раза, используя @TestFactory, меняя количество созданий и удалений на 2:1, 5:2, 1:3 соответственно.
            """)
    List<DynamicTest> addRemoveElementsFactoryTest() {
        List<DynamicTest> tests = new ArrayList<>();
        // Добавляем три теста с различными соотношениями добавлений/удалений
        tests.add(createTestForFactory(2, 1));
        tests.add(createTestForFactory(5, 2));
        tests.add(createTestForFactory(1, 3));
        return tests;
    }

    private DynamicTest createTestForFactory(int addCount, int deleteCount) {
        return DynamicTest.dynamicTest("Создаём: " + addCount + " , удаляем: " + deleteCount,
                () -> {
                    openBaseUrl();
                    SelenideElement addRemoveElementsButton = $x("//a[@href='/add_remove_elements/']");
                    steps.clickLink(addRemoveElementsButton, addRemoveElementsButton.getText());
                    steps.addElements(addCount);
                    steps.deleteElements(deleteCount);
                }
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"200", "301", "404", "500"})
    @DisplayName("Status Code")
    @Description("""
            Перейти на страницу Status Codes.
            Кликнуть на каждый статус и вывести текст после перехода на страницу статуса.
            Добавить проверки в задание Status Codes.
            Добавить Проверку, что переход был осуществлен на страницу с корректным статусом.
            """)
    public void statusCodeTest(String status) {
        SelenideElement statusCodesButton = $x("//a[@href='/status_codes']");
        steps.clickLink(statusCodesButton, statusCodesButton.getText());
        steps.clickAndCheckStatus(status);
    }

    // Метод для добавления скриншота в отчет Allure
    public void attachScreenshot() {
        File screenshotFile = Screenshots.takeScreenShotAsFile();
        if (screenshotFile != null) {
            try {
                byte[] screenshotBytes = Files.readAllBytes(screenshotFile.toPath());
                addAttachment("Финальный скриншот", "image/png", new ByteArrayInputStream(screenshotBytes), "png");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Скриншот не был создан, так как screenshotFile равен null.");
        }
    }

    @AfterEach
    void teardown() {
        attachScreenshot();
        closeWebDriver();
    }
}
