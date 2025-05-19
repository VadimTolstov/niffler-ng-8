package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SpendingTable;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

@ParametersAreNonnullByDefault
public class EditSpendingPage extends BasePage<EditSpendingPage> {

    protected SpendingTable spendingTable = new SpendingTable();

    private final SelenideElement descriptionInput;
    private final SelenideElement submitBtn;
    private final SelenideElement heading ;

    protected EditSpendingPage (SelenideDriver selenideDriver) {
        this.descriptionInput = selenideDriver.$("#description");
        this.submitBtn = selenideDriver.$("#save");
        this.heading = selenideDriver.$x("//h2[text()='Add new spending']");
    }
    public EditSpendingPage () {
        this.descriptionInput = $("#description");
        this.submitBtn = $("#save");
        this.heading = $x("//h2[text()='Add new spending']");
    }

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

    @Step("Создаем новую трату")
    public EditSpendingPage editDescription(String description) {
        descriptionInput.clear();
        descriptionInput.setValue(description);
        submitBtn.click();
        return this;
    }

//    public EditSpendingPage editSpending(String spendingDescription) {
//        tableRows.find(text(spendingDescription))
//                .$$("td")
//                .get(5)
//                .click();
//        return new EditSpendingPage();
//    }
}