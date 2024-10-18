package tests;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.util.Random;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.WebDriverRunner.url;
import static org.junit.jupiter.api.Assertions.fail;

public class TestSteps {
    @Step("Перейти на страницу {buttonName}")
    public void clickLink(SelenideElement buttonElement, String buttonName) {
        buttonElement.click();
    }

    @Step("Кликнуть на чекбокс и проверить его состояние.")
    public void setCheckboxWithVerification(SelenideElement checkbox) {
        boolean isCheckedBeforeClick = checkbox.isSelected();

        checkbox.click();
        if (isCheckedBeforeClick) {
            checkbox.shouldNotBe(Condition.checked);
        } else {
            checkbox.shouldBe(Condition.checked);
        }
    }

    @Step("Вывести в консоль значение атрибута checked для {checkboxName}.")
    public void printCheckedAttribute(SelenideElement checkbox, String checkboxName) {
        System.out.println("Значение атрибута checked для " + checkboxName + ": " + checkbox.getAttribute("checked"));
    }

    @Step("Выбрать опцию, вывести в консоль текущий текст элемента dropdown. " +
            "Проверка корректного состояние каждого dropDown после каждого нажатия на него. ")
    public void selectOption(SelenideElement dropdownElement, int optionNumber) {
        dropdownElement.selectOption(optionNumber);
        String selectedText = dropdownElement.getText();
        System.out.println("В выпадающем списке выбрана опция: " + selectedText);

        dropdownElement.shouldHave(text(selectedText));
    }

    @Step("Добиться отображения 5 элементов, максимум за 10 попыток, если нет, провалить тест с ошибкой. " +
            "Для каждого обновления страницы проверяем наличие 5 элементов.")
    public void check5Elements() {
        ElementsCollection disappearingElementsList = $$x("//li/a");

        if (disappearingElementsList.size() == 5) {
            System.out.println("Найдено 5 элементов");
        }
        disappearingElementsList.should(CollectionCondition.size(5));
    }


    @Step("Ввести любое случайное число от 1 до 10 000. Вывести в консоль значение элемента Input." +
            "Проверить, что в поле ввода отображается именно то число, которое было введено.")
    public void enterRandomNumberInInput(SelenideElement inputField) {
        int randomNumber = (int) (Math.random() * 10000) + 1;
        inputField.sendKeys(String.valueOf(randomNumber));
        System.out.println("Значение элемента Input: " + inputField.getValue());
        inputField.shouldBe(Condition.value(String.valueOf(randomNumber)));
    }

    @Step("Проверка ввода недопустимого значения: '{value}'")
    public void checkInvalidInput(SelenideElement inputField, String value) {
        String expectedValue = value.replaceAll("[^0-9]", "");
        if (expectedValue.isEmpty()) {
            inputField.shouldBe(Condition.empty);
        } else {
            inputField.shouldBe(Condition.value(expectedValue));
        }

    }

    @Step("Навести курсор на картинку {0} и проверить текст")
    public void hoverOnImageAndCheckText(int imageIndex, String expectedText) {
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
    public void checkNotification() {
        SelenideElement clickHereButton = $x("//a[text()='Click here']");
        SelenideElement notificationMessage = $x("//div[@id='flash']");
        String messageText = notificationMessage.getText().trim();

        clickHereButton.click();
        notificationMessage.shouldHave(Condition.text("Action successful"));
        System.out.println("Уведомление успешно: " + messageText);
    }

    @Step("Закрыть всплывающее уведомление.")
    public void closeNotification(SelenideElement notificationMessage) {
        SelenideElement closeButton = notificationMessage.$x(".//a[contains(@class,'close')]");
        closeButton.click();
    }

    @Step("Получить последний добавленный элемент.")
    public SelenideElement getLastAddedElement() {
        return $$x("//button[text()='Delete']").last();
    }

    @Step("Нажать на  кнопку Add Element {0} раз. Выводить в консоль количество добавленных кнопок их тексты.")
    public void addElements(int count) {
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
    public void deleteElements(int count) {
        Random random = new Random();

        for (int i = 1; i <= count; i++) {
            ElementsCollection elementsCollection = $$x("//button[text()='Delete']");
            int countOfDeleteButtons = elementsCollection.size();
            if (elementsCollection.isEmpty()) {
                System.out.println("Нет больше кнопок Delete для удаления.");
                elementsCollection.shouldHave(CollectionCondition.sizeGreaterThanOrEqual(count - i + 1));
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
    public void printDeleteButtons() {
        ElementsCollection deleteButtons = $$x("//button[text()='Delete']");
        System.out.println("Оставшееся количество кнопок Delete: " + deleteButtons.size());

        for (int j = 0; j < deleteButtons.size(); j++) {
            System.out.println("Текст кнопки №" + (j + 1) + ": " + deleteButtons.get(j).getText());
        }
    }

    @Step("Кликнуть на статус {status} и вывести текст страницы статуса. Переход был осуществлен на страницу с корректным статусом")
    public void clickAndCheckStatus(String status) {
        SelenideElement statusLink = $x("//a[contains(text(), '" + status + "')]");
        statusLink.click();
        SelenideElement pageTextElement = $x("//div[@class='example']/p");
        pageTextElement.shouldHave(text("This page returned a " + status + " status code."));
        url().contains(status);

        String pageText = pageTextElement.getText();
        String statusMessage = pageText.split("For a definition")[0].trim();
        System.out.println("Текст страницы статуса (" + status + "): \n" + statusMessage);
    }
}
