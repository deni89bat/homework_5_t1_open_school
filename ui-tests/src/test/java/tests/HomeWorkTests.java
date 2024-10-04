package tests;

import com.codeborne.selenide.*;
import com.codeborne.selenide.junit5.ScreenShooterExtension;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Random;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.addAttachment;

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
    @Description("Перейти на страницу Checkboxes. Выделить первый чекбокс, снять выделение со второго чекбокса. Вывести в консоль значение атрибута checked для каждого чекбокса." +
            "Проверять корректное состояние каждого чекбокса после каждого нажатия на него. Запустить тест с помощью @ParametrizedTest, изменяя порядок нажатия на чекбоксы с помощью одного параметра.")
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
    @Description("Перейти на страницу Dropdown. Выбрать первую опцию, вывести в консоль текущий текст элемента dropdown, выбрать вторую опцию, вывести в консоль текущий текст элемента dropdown." +
            "Проверять корректное состояние каждого dropDown после каждого нажатия на него. ")
    public void dropdownTest() {
        SelenideElement dropdownButton = $x("//a[@href='/dropdown']");
        SelenideElement dropdownElement = $x("//select[@id='dropdown']");

        clickLink(dropdownButton, dropdownButton.getText());
        selectOption(dropdownElement, 1);
        selectOption(dropdownElement, 2);
    }

    @RepeatedTest(failureThreshold = 5, value = 10,  name = "Запуск {currentRepetition} из {totalRepetitions}" )
    @DisplayName("Disappearing Elements")
    @Description("Перейти на страницу Disappearing Elements. Добиться отображения 5 элементов, максимум за 10 попыток, если нет, провалить тест с ошибкой." +
            "Для каждого обновления страницы проверять наличие 5 элементов. Использовать @RepeatedTest")
    public void disappearingElementsTest() {
        SelenideElement disappearingElementsButton = $x("//a[@href='/disappearing_elements']");
        clickLink(disappearingElementsButton, disappearingElementsButton.getText());
        check5Elements();
    }

    @Test
    @DisplayName("Inputs")
    @Description("Перейти на страницу Inputs. Ввести любое случайное число от 1 до 10 000. Вывести в консоль значение элемента Input.")
    public void inputsTest() {
        SelenideElement inputsButton = $x("//a[@href='/inputs']");
        SelenideElement inputField = $x("//input");
        clickLink(inputsButton, inputsButton.getText());
        enterRandomNumberInInput(inputField);
    }

    @Test
    @DisplayName("Hover")
    @Description("Перейти на страницу Hovers. Навести курсор на каждую картинку. Вывести в консоль текст, который появляется при наведении.")
    public void hoverTest() {
        SelenideElement hoversButton = $x("//a[@href='/hovers']");
        clickLink(hoversButton, hoversButton.getText());
        hoverOverImagesAndPrintText();
    }

    @Test
    @DisplayName("Notification Message")
    @Description("Перейти на страницу Notification Message. Кликать до тех пор, пока не покажется уведомление Action successful. После каждого неудачного клика закрывать всплывающее уведомление.")
    public void notificationMessageTest() {
        SelenideElement notificationMessageButton = $x("//a[@href='/notification_message']");
        clickLink(notificationMessageButton, notificationMessageButton.getText());
        clickUntilSuccessNotification();
    }

    @Test
    @DisplayName("Add/Remove Elements")
    @Description("Перейти на страницу Add/Remove Elements. Нажать на кнопку Add Element 5 раз. С каждым нажатием выводить в консоль текст появившегося элемента. Нажать на разные кнопки Delete три раза. Выводить в консоль оставшееся количество кнопок Delete и их тексты.")
    public void addRemoveElementsTest() {
        SelenideElement addRemoveElementsButton = $x("//a[@href='/add_remove_elements/']");
        clickLink(addRemoveElementsButton, addRemoveElementsButton.getText());
        addElements(5);
        deleteElements(3);
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


    @Step("Ввести любое случайное число от 1 до 10 000. Вывести в консоль значение элемента Input.")
    private void enterRandomNumberInInput(SelenideElement inputField) {
        int randomNumber = (int) (Math.random() * 10000) + 1;
        inputField.sendKeys(String.valueOf(randomNumber));
        System.out.println("Значение элемента Input: " + inputField.getValue());
    }


    @Step("Навести курсор на каждую картинку и вывести в консоль текст, который появляется при наведении.")
    private void hoverOverImagesAndPrintText() {
        ElementsCollection images = $$x("//div[@class='figure']");

        for (int i = 0; i < images.size(); i++) {
            hoverOnImageAndPrintText(images.get(i), i + 1);
        }
    }

    @Step("Навести курсор на картинку {imageNumber} и вывести текст.")
    private void hoverOnImageAndPrintText(SelenideElement image, int imageNumber) {
        image.hover();  // Наводим курсор на изображение

        // Захватываем текст, который появляется при наведении
        SelenideElement caption = image.$x(".//div[@class='figcaption']");
        System.out.println("Текст, появившийся при наведении на изображение " + imageNumber + ":  \n" + caption.getText());
    }

    @Step("Кликать до тех пор, пока не покажется уведомление Action successful. После каждого неудачного клика закрывать всплывающее уведомление.")
    private void clickUntilSuccessNotification() {
        SelenideElement clickHereButton = $x("//a[text()='Click here']");
        SelenideElement notificationMessage = $x("//div[@id='flash']");
        String expectedMessage = "Action successful";

        boolean isSuccess = false;
        while (!isSuccess) {
            clickHereButton.click();
            String messageText = notificationMessage.getText().trim();
            if (messageText.contains(expectedMessage)) {
                isSuccess = true;
                System.out.println("Уведомление успешно: " + messageText);
            } else {
                System.out.println("Уведомление: " + messageText);
                closeNotification(notificationMessage);
            }
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

    @Step("Нажать на случайные кнопки Delete {0} раз. Выводить в консоль оставшееся количество кнопок Delete и их тексты.")
    private void deleteElements(int count) {
        Random random = new Random();

        for (int i = 1; i <= count; i++) {
            ElementsCollection elementsCollection = $$x("//button[text()='Delete']");
            if (elementsCollection.isEmpty()) {
                System.out.println("Нет больше кнопок Delete для удаления.");
                break;
            }
            int randomIndex = random.nextInt(elementsCollection.size());
            elementsCollection.get(randomIndex).click();
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
