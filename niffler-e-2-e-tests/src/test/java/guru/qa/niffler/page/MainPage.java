package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;
import io.qameta.allure.Step;
import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {
    public static final String URL = CFG.frontUrl() + "main";

    @Getter
    private final Header header = new Header();

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
        tableRows.find(text(spendingDescription))
                .should(visible);
    }


}
