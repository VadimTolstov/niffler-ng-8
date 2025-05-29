package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SpendingTable;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

@ParametersAreNonnullByDefault
public class EditSpendingPage extends BasePage<EditSpendingPage> {

    protected SpendingTable spendingTable = new SpendingTable();

    private final SelenideElement descriptionInput = $("#description");
    private final SelenideElement submitBtn = $("#save");
    private final SelenideElement heading = $x("//h2[text()='Add new spending']");
    private final SelenideElement categoryInput = $("#category");
    private final SelenideElement amountInput = $("#amount");


    @Nonnull
    public SpendingTable getSpendingTable() {
        spendingTable.getSelf().scrollIntoView(true);
        return spendingTable;
    }

    @Step("Проверяем, что загрузилась страница трат")
    @Override
    public EditSpendingPage checkThatPageLoaded() {
        heading.shouldHave(Condition.visible);
        return this;
    }

    @Step("Ввести название траты {description}")
    public EditSpendingPage setSpendingDescription(String description) {
        descriptionInput.clear();
        descriptionInput.setValue(description);
        return this;
    }

    @Step("Сохранить изменения")
    public EditSpendingPage saveSpending() {
        submitBtn.click();
        return this;
    }

    @Step("Ввести название категории: {category}")
    public EditSpendingPage setSpendingCategory(String category) {
        categoryInput.setValue(category);
        return this;
    }

    @Step("Ввести сумму траты: {amount}")
    public EditSpendingPage setSpendingAmount(String amount) {
        amountInput.setValue(amount);
        return this;
    }
}