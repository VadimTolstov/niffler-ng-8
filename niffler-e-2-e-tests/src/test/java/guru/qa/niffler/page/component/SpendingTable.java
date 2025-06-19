package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.page.EditSpendingPage;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byTagAndText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static guru.qa.niffler.condition.SpendsConditions.spendsInAnyOrder;

@ParametersAreNonnullByDefault
public class SpendingTable extends BaseComponent<SpendingTable> {

    public SpendingTable() {
        super($("#spendings"));
    }

    private final SelenideElement self = $("#spendings");
    private final ElementsCollection spendingRows = self.$$("tbody tr");
    private final SelenideElement modalDialog = $("div[role='dialog']");

    @Step("Выбрать период: {period}")
    public SpendingTable selectPeriod(DataFilterValues period) {
        self.$("#period").click();
        $("li[data-value='" + period.name() + "']").click();
        return this;
    }

    @Step("Редактировать трату: {description}")
    public EditSpendingPage editSpending(String description) {
        spendingRows.find(text(description)).$("button[aria-label='Edit spending']").click();
        return new EditSpendingPage();
    }

    @Step("Удалить трату: {description}")
    public SpendingTable deleteSpending(String description) {
        spendingRows.find(text(description)).$("input[type='checkbox']").click();
        self.$("#delete").click();
        modalDialog.$(byTagAndText("button", "Delete")).click();
        return this;
    }

    @Step("Поиск траты: {description}")
    public SpendingTable searchSpendingByDescription(String description) {
        spendingRows.find(text(description)).shouldBe(visible);
        return this;
    }

    @Step("Проверить, что таблица содержит траты: {expectedSpends}")
    public SpendingTable checkTableContains(String... expectedSpends) {
        for (String spend : expectedSpends) {
            spendingRows.find(text(spend)).shouldBe(visible);
        }
        return this;
    }

    @Step("Проверить количество трат: {expectedSize}")
    public SpendingTable checkTableSize(int expectedSize) {
        spendingRows.shouldHave(size(expectedSize));
        return this;
    }

    @Step("Проверить, что заголовок таблицы виден")
    public void titleIsVisible() {
        self.$("h2").shouldHave(text("History of Spendings"));
    }

    @Step("Проверяем цвет и текст траты {expectedColors}")
    @Nonnull
    public SpendingTable checkSpendsValues(SpendJson... expectedSpends) {
        spendingRows.should(spendsInAnyOrder(expectedSpends));
        return this;
    }
}
