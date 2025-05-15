package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SearchField;
import guru.qa.niffler.page.component.SpendingTable;
import guru.qa.niffler.page.component.StatComponent;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {
    protected final SearchField searchField = new SearchField();
    protected final StatComponent statComponent = new StatComponent();
    protected final SpendingTable spendingTable = new SpendingTable();

    public static final String URL = CFG.frontUrl() + "main";

    private final SelenideElement tableHistoryOfSpendings = $("#spendings");

    @Nonnull
    public StatComponent getStatComponent() {
        statComponent.getSelf().scrollIntoView(true);
        return statComponent;
    }

    @Nonnull
    public SearchField getSearchField() {
        statComponent.getSelf().scrollIntoView(true);
        return searchField;
    }

    @Nonnull
    public SpendingTable getSpendingTable() {
        spendingTable.getSelf().scrollIntoView(true);
        return spendingTable;
    }

    @Step("Проверяем, что загрузилась главная страница")
    @Override
    public MainPage checkThatPageLoaded() {
        tableHistoryOfSpendings.shouldHave(visible);
        return this;
    }
}
