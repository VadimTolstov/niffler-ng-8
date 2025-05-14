package guru.qa.niffler.page.component;

import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class SearchField extends BaseComponent<SearchField> {

    public SearchField() {
        super($("input[placeholder='Search']"));
    }

    @Step("Поиск по запросу: {query}")
    public SearchField search(String query) {
        self.setValue(query).pressEnter();
        return this;
    }

    @Step("Очистить поле поиска")
    public SearchField clear() {
        self.clear();
        return this;
    }
}