package guru.qa.niffler.page.component;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public class PeopleTable {

    private final SelenideElement self = $("#root header");
    private final SelenideElement peopleTable = self.$("div[role='tabpanel']");
    private final ElementsCollection allPeopleList = $$("tr.MuiTableRow-root");

    @Step("Проверяем отображение пользователя: {users}")
    public PeopleTable checkPeople(String user) {
            allPeopleList.filterBy(Condition.text(user));
            return this;
        }


}
