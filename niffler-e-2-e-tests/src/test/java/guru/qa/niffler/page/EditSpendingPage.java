package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

@ParametersAreNonnullByDefault
public class EditSpendingPage extends BasePage<EditSpendingPage> {

    private final SelenideElement descriptionInput = $("#description");
    private final SelenideElement submitBtn = $("#save");
    private final SelenideElement heading = $x("//h2[text()='Add new spending']");

    @Step("Проверяем, что загрузилась страница трат")
    @Override
    public EditSpendingPage checkThatPageLoaded() {
        heading.shouldHave(Condition.visible);
        return this;
    }

    @Step("Создаем новую трату")
    public void editDescription(String description) {
        descriptionInput.clear();
        descriptionInput.setValue(description);
        submitBtn.click();
    }
}