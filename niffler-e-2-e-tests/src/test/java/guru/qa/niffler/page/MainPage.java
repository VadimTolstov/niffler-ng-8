package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SearchField;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {
    SearchField searchField = new SearchField();

    public static final String URL = CFG.frontUrl() + "main";

    private final SelenideElement statImg = $("canvas[role='img']");
    private final SelenideElement statCell = $("#legend-container");
    private final ElementsCollection tableRows = $$("#spendings tbody tr");
    private final SelenideElement tableHistoryOfSpendings = $("#spendings");
    private final SelenideElement deleteBtn = tableHistoryOfSpendings.$("#delete");
    private final SelenideElement popup = $("div[role='dialog']");


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

    public MainPage checkThatTableContains(String spendingDescription) {
        searchField.search(spendingDescription);
        tableRows.find(text(spendingDescription))
                .should(visible);
        return this;
    }

    @Step("Убедитесь, что статистическое изображение соответствует ожидаемому")
    public MainPage checkStatImg(BufferedImage expected) throws IOException {
        sleep(3000);
        BufferedImage actual = ImageIO.read(Objects.requireNonNull(statImg.screenshot()));
        assertFalse(new ScreenDiffResult(
                        expected, actual),
                "Screen comparison failure"
        );
        return this;
    }

    @Step("Убедитесь, что в статистической ячейке есть текст {text}")
    public MainPage checkStatCell(String... text) {
        for (String s : text) {
            statCell.shouldHave(exactText(s));
        }
        return this;
    }

    @Step("Удалить трату: {description}")
    @Nonnull
    public MainPage deleteSpending(String description) {
        SelenideElement row = tableRows.find(text(description));
        row.$$("td").get(0).click();
        deleteBtn.click();
        popup.$(byText("Delete")).click(usingJavaScript());
        return this;
    }

    @Step("Проверить количество трат: {expectedSize}")
    @Nonnull
    public void checkTableSize(int expectedSize) {
        tableRows.shouldHave(size(expectedSize));
    }
}
