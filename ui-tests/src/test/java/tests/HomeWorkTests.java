package tests;

import com.codeborne.selenide.*;
import com.codeborne.selenide.junit5.ScreenShooterExtension;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
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
import static io.qameta.allure.Allure.addAttachment;
import static org.junit.jupiter.api.Assertions.fail;

public class HomeWorkTests {
    // Регистрация расширения для создания скриншотов
    @RegisterExtension
    static ScreenShooterExtension screenshotEmAll = new ScreenShooterExtension(true).to("target/screenshots");

    @BeforeAll
    public static void setup() {
        Configuration.browser = "chrome";
        Configuration.pageLoadStrategy = "eager";
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

        clickLink(checkboxesButton, checkboxesButton.getText());

        if (Objects.equals(order, "ascOrder")) {
            setCheckboxWithVerification(checkbox1);
            setCheckboxWithVerification(checkbox2);
        } else {
            setCheckboxWithVerification(checkbox2);
            setCheckboxWithVerification(checkbox1);
        }

        printCheckedAttribute(checkbox1, "checkbox 1");
        printCheckedAttribute(checkbox2, "checkbox 2");
    }

    @Test
    @DisplayName("Dropdown")
    @Description("""
            Перейти на страницу Dropdown. Выбрать первую опцию, вывести в консоль текущий текст элемента dropdown, выбрать вторую опцию, вывести в консоль текущий текст элемента dropdown.
            Проверять корректное состояние каждого dropDown после каждого нажатия на него.""")
    public void dropdownTest() {
        SelenideElement dropdownButton = $x("//a[@href='/dropdown']");
        SelenideElement dropdownElement = $x("//select[@id='dropdown']");

        clickLink(dropdownButton, dropdownButton.getText());
        selectOption(dropdownElement, 1);
        selectOption(dropdownElement, 2);
    }

    @RepeatedTest(failureThreshold = 5, value = 10, name = "Запуск {currentRepetition} из {totalRepetitions}")
    @DisplayName("Disappearing Elements")
    @Description("""
            Перейти на страницу Disappearing Elements. Добиться отображения 5 элементов, максимум за 10 попыток, если нет, провалить тест с ошибкой.
            Для каждого обновления страницы проверять наличие 5 элементов. Использовать @RepeatedTest""")
    public void disappearingElementsTest() {
        SelenideElement disappearingElementsButton = $x("//a[@href='/disappearing_elements']");
        clickLink(disappearingElementsButton, disappearingElementsButton.getText());
        check5Elements();
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
                            clickLink(inputsButton, inputsButton.getText());  // Кликаем на ссылку только в первом тесте
                        }
                        inputField.clear();  // Очищаем поле перед каждым вводом
                        enterRandomNumberInInput(inputField);  // Вводим случайное число
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
                        checkInvalidInput(inputField, value);
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
        clickLink(hoversButton, hoversButton.getText());
        hoverOnImageAndCheckText(imageIndex, expectedText);
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
        clickLink(notificationMessageButton, notificationMessageButton.getText());
        checkNotification();
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
                    clickLink(addRemoveElementsButton, addRemoveElementsButton.getText());
                    addElement(addCount);
                    deleteElements(deleteCount);
                }
        );
    }

    @Test
    @DisplayName("Status Code 200")
    @Description("Перейти на страницу Status Codes. Кликнуть на каждый статус в новом тестовом методе, вывести на экран текст после перехода на страницу статуса.")
    public void statusCode200Test() {
        SelenideElement statusCodesButton = $x("//a[@href='/status_codes']");
        clickLink(statusCodesButton, statusCodesButton.getText());
        clickStatusAndPrintText("200");
    }

    @Test
    @DisplayName("Status Code 301")
    @Description("Перейти на страницу Status Codes. Кликнуть на каждый статус в новом тестовом методе, вывести на экран текст после перехода на страницу статуса.")
    public void statusCode301Test() {
        SelenideElement statusCodesButton = $x("//a[@href='/status_codes']");
        clickLink(statusCodesButton, statusCodesButton.getText());
        clickStatusAndPrintText("301");
    }

    @Test
    @DisplayName("Status Code 404")
    @Description("Перейти на страницу Status Codes. Кликнуть на каждый статус в новом тестовом методе, вывести на экран текст после перехода на страницу статуса.")
    public void statusCode404Test() {
        SelenideElement statusCodesButton = $x("//a[@href='/status_codes']");
        clickLink(statusCodesButton, statusCodesButton.getText());
        clickStatusAndPrintText("404");
    }

    @Test
    @DisplayName("Status Code 500")
    @Description("Перейти на страницу Status Codes. Кликнуть на каждый статус в новом тестовом методе, вывести на экран текст после перехода на страницу статуса.")
    public void statusCode500Test() {
        SelenideElement statusCodesButton = $x("//a[@href='/status_codes']");
        clickLink(statusCodesButton, statusCodesButton.getText());
        clickStatusAndPrintText("500");
    }

    @Step("Кликнуть на статус {status} и вывести текст страницы статуса.")
    private void clickStatusAndPrintText(String status) {
        SelenideElement statusLink = $x("//a[contains(text(), '" + status + "')]");
        statusLink.click();

        String pageText = $x("//div[@class='example']/p").getText();
        String statusMessage = pageText.split("For a definition")[0].trim();
        System.out.println("Текст страницы статуса (" + status + "): \n" + statusMessage);
    }

    @Step("Перейти на страницу {buttonName}")
    private void clickLink(SelenideElement buttonElement, String buttonName) {
        buttonElement.click();
    }

    @Step("Кликнуть на чекбокс и проверить его состояние.")
    private void setCheckboxWithVerification(SelenideElement checkbox) {
        boolean isCheckedBeforeClick = checkbox.isSelected();

        checkbox.click();
        if (isCheckedBeforeClick) {
            checkbox.shouldNotBe(Condition.checked);
        } else {
            checkbox.shouldBe(Condition.checked);
        }
    }

    @Step("Вывести в консоль значение атрибута checked для {checkboxName}.")
    private void printCheckedAttribute(SelenideElement checkbox, String checkboxName) {
        System.out.println("Значение атрибута checked для " + checkboxName + ": " + checkbox.getAttribute("checked"));
    }

    @Step("Выбрать опцию, вывести в консоль текущий текст элемента dropdown. " +
            "Проверка корректного состояние каждого dropDown после каждого нажатия на него. ")
    private void selectOption(SelenideElement dropdownElement, int optionNumber) {
        dropdownElement.selectOption(optionNumber);
        String selectedText = dropdownElement.getText();
        System.out.println("В выпадающем списке выбрана опция: " + selectedText);

        dropdownElement.shouldHave(text(selectedText));
    }

    @Step("Добиться отображения 5 элементов, максимум за 10 попыток, если нет, провалить тест с ошибкой. " +
            "Для каждого обновления страницы проверяем наличие 5 элементов.")
    private void check5Elements() {
        int numberOfAttempts = 10;

        for (int i = 1; i <= numberOfAttempts; i++) {
            ElementsCollection disappearingElementsList = $$x("//li/a");

            if (disappearingElementsList.size() == 5) {
                System.out.println("Найдено 5 элементов на попытке №" + (i));
                disappearingElementsList.should(CollectionCondition.size(5));
                break;
            }

            System.out.println("Количество обновлений - " + i);
            refresh();
        }
    }


    @Step("Ввести любое случайное число от 1 до 10 000. Вывести в консоль значение элемента Input." +
            "Проверить, что в поле ввода отображается именно то число, которое было введено.")
    private void enterRandomNumberInInput(SelenideElement inputField) {
        int randomNumber = (int) (Math.random() * 10000) + 1;
        inputField.sendKeys(String.valueOf(randomNumber));
        System.out.println("Значение элемента Input: " + inputField.getValue());
        inputField.shouldBe(Condition.value(String.valueOf(randomNumber)));
    }

    @Step("Проверка ввода недопустимого значения: '{value}'")
    private void checkInvalidInput(SelenideElement inputField, String value) {
        String expectedValue = value.replaceAll("[^0-9]", "");
        if (expectedValue.isEmpty()) {
            inputField.shouldBe(Condition.empty);
        } else {
            inputField.shouldBe(Condition.value(expectedValue));
        }

    }

    @Step("Навести курсор на картинку {0} и проверить текст")
    private void hoverOnImageAndCheckText(int imageIndex, String expectedText) {
        SelenideElement image = $x(String.format("//div[@class='figure'][%s]", imageIndex));
        sleep(200);

        image.shouldBe(Condition.visible);
        image.hover();

        SelenideElement caption = image.$x(".//div[@class='figcaption']");
        caption.shouldBe(Condition.visible);
        caption.shouldHave(Condition.text(expectedText));

        System.out.println("Текст, появившийся при наведении на изображение " + imageIndex + ":  \n" + caption.getText());
    }

    @Step("Проверяем, что всплывающее уведомление Successfull. " +
            "Если нет - то закрываем всплывающее уведомление и кликаем кнопку Click Here, повторно проверяем всплывающее уведомление Successfull.")
    private void checkNotification() {
        SelenideElement clickHereButton = $x("//a[text()='Click here']");
        SelenideElement notificationMessage = $x("//div[@id='flash']");
        String expectedMessage = "Action successful";
        String messageText = notificationMessage.getText().trim();

        if (messageText.contains(expectedMessage)) {
            System.out.println("Уведомление успешно: " + messageText);
            notificationMessage.shouldHave(Condition.text(expectedMessage));
        } else {
            System.out.println("Уведомление не успешно: " + messageText);
            closeNotification(notificationMessage);
            clickHereButton.click();
            System.out.println("Уведомление после нажатия clickHere: " + notificationMessage.getText().trim());
            notificationMessage.shouldHave(Condition.text(expectedMessage));
        }
    }

    @Step("Закрыть всплывающее уведомление.")
    private void closeNotification(SelenideElement notificationMessage) {
        SelenideElement closeButton = notificationMessage.$x(".//a[contains(@class,'close')]");
        closeButton.click();
    }

    @Step("Нажать на кнопку Add Element {0} раз. С каждым нажатием выводить в консоль текст появившегося элемента.")
    private void addElements(int count) {
        SelenideElement addButton = $x("//button[text()='Add Element']");

        for (int i = 1; i <= count; i++) {
            addButton.click();
            System.out.println("Добавлен элемент с текстом: " + getLastAddedElement().getText());
        }
    }

    @Step("Получить последний добавленный элемент.")
    private SelenideElement getLastAddedElement() {
        return $$x("//button[text()='Delete']").last();
    }

    @Step("Нажать на  кнопку Add Element {0} раз. Выводить в консоль количество добавленных кнопок их тексты.")
    private void addElement(int count) {
        for (int i = 1; i <= count; i++) {
            $x("//button[text()='Add Element']").click();
            System.out.println("Добавлен элемент №" + i);
            ElementsCollection deleteButtons = $$x("//button[text()='Delete']");
            deleteButtons.should(CollectionCondition.size(i));
            SelenideElement addedElement = getLastAddedElement();
            System.out.println("Текст добавленного элемента: " + addedElement.getText());
        }
    }

    @Step("Нажать на случайные кнопки Delete {0} раз. Выводить в консоль оставшееся количество кнопок Delete и их тексты.")
    private void deleteElements(int count) {
        Random random = new Random();

        for (int i = 1; i <= count; i++) {
            ElementsCollection elementsCollection = $$x("//button[text()='Delete']");
            int countOfDeleteButtons = elementsCollection.size();
            if (elementsCollection.isEmpty()) {
                System.out.println("Нет больше кнопок Delete для удаления.");
                fail("Недостаточно кнопок для удаления. Ожидалось: " + (count - i + 1) + ", но доступно: " + elementsCollection.size());
            }
            int randomIndex = random.nextInt(elementsCollection.size());
            elementsCollection.get(randomIndex).click();
            //Проверка, что кнопка удалилась и осталось ожидаемые кол-во кнопок
            elementsCollection.should(CollectionCondition.size(countOfDeleteButtons - 1));
            System.out.println("Удалена случайная кнопка Delete №" + (randomIndex + 1));
            printDeleteButtons();
        }
    }

    @Step("Вывести в консоль оставшееся количество кнопок Delete и их тексты.")
    private void printDeleteButtons() {
        ElementsCollection deleteButtons = $$x("//button[text()='Delete']");
        System.out.println("Оставшееся количество кнопок Delete: " + deleteButtons.size());

        for (int j = 0; j < deleteButtons.size(); j++) {
            System.out.println("Текст кнопки №" + (j + 1) + ": " + deleteButtons.get(j).getText());
        }
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
