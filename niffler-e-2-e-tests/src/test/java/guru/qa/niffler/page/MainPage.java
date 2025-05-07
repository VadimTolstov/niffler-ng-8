package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SearchField;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {
    SearchField searchField = new SearchField();

    public static final String URL = CFG.frontUrl() + "main";

    private final ElementsCollection tableRows = $$("#spendings tbody tr");
    private final SelenideElement tableHistoryOfSpendings = $("#spendings");


    @Step("Проверяем, что загрузилась главная страница")
    @Override
    public MainPage checkThatPageLoaded() {
        tableHistoryOfSpendings.shouldHave(visible);
        return this;
    }

    public EditSpendingPage editSpending(String spendingDescription) {
        tableRows.find(text(spendingDescription))
                .$$("td")
                .get(5)
                .click();
        return new EditSpendingPage();
    }

    public void checkThatTableContains(String spendingDescription) {
        searchField.search(spendingDescription);
        tableRows.find(text(spendingDescription))
                .should(visible);
    }


}
