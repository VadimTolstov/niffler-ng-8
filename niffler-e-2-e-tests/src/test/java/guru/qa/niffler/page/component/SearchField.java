package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class SearchField {
    private final SelenideElement search = $("input[aria-label='search']");

    @Step("Поиск по запросу: {query}")
    @Nonnull
    public SearchField search(String query) {
        search.setValue(query).pressEnter();
        return this;
    }
}