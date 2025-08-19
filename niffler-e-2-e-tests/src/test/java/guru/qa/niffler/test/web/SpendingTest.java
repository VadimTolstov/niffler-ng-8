package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.condition.Bubble;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.io.IOException;

@WebTest
@ParametersAreNonnullByDefault
public class SpendingTest {

    private static final Config CFG = Config.getInstance();

    @User
    @ApiLogin
    @Test
    @DisplayName("Добавление новой траты и проверка сообщения")
    void checkAlertSpending() {
        final String newDescription = "Обучение Niffler NG";
        final String newCategory = "Обучение";

        Selenide.open(MainPage.URL, MainPage.class)
                .getHeaderComponent()
                .addSpendingPage()
                .setSpendingDescription(newDescription)
                .setSpendingAmount("1000")
                .setSpendingCategory(newCategory)
                .saveSpending()
                .checkAlert("New spending is successfully created")
                .getSpendingTable()
                .checkTableContains(newDescription);

    }

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Niffler 2.0",
                    amount = 89000.00,
                    currency = CurrencyValues.RUB
            )
    )
    @ApiLogin
    @Test
    void spendingDescriptionShouldBeUpdatedByTableAction(UserJson user) {
        final String newDescription = "Обучение Niffler NG";

        Selenide.open(MainPage.URL, MainPage.class)
                .getHeaderComponent()
                .addSpendingPage()
                .setSpendingAmount(user.testData().spendings().getFirst().amount().toString())
                .setSpendingCategory(user.testData().spendings().getFirst().category().name())
                .setSpendingDescription(user.testData().spendings().getFirst().description())
                .setSpendingDescription(newDescription)
                .saveSpending()
                .checkAlert("New spending is successfully created")
                .getSpendingTable()
                .checkTableContains(newDescription);

    }

    @User(
            spendings = {
                    @Spending(
                            category = "Обучение",
                            description = "Обучение Advanced 2.0",
                            amount = 799910
                    ),
                    @Spending(
                            category = "Ужин",
                            description = "Ужин в кафе",
                            amount = 5000
                    )
            }
    )
    @ApiLogin
    @ScreenShotTest(expected = "expected-stat.png")
    void checkStatComponentTest(@Nonnull UserJson user, BufferedImage expected) throws IOException, InterruptedException {
        Selenide.open(MainPage.URL, MainPage.class)
                .checkThatPageLoaded()
                .getStatComponent()
                .checkStatImg(expected)
                .checkStatText("Обучение 799910 ₽")
                .checkBubblesAndText(
                        new Bubble(Color.yellow, "Обучение 799910 ₽"),
                        new Bubble(Color.green, "Ужин 5000 ₽")
                );

    }

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )

    @ApiLogin
    @ScreenShotTest(expected = "clear-stat.png")
    void deleteSpendingTest(@Nonnull UserJson user, BufferedImage clearStat) throws IOException {
        Selenide.open(MainPage.URL, MainPage.class)
                .checkThatPageLoaded()
                .getSpendingTable()
                .checkTableContains("Обучение Advanced 2.0")
                .deleteSpending("Обучение Advanced 2.0")
                .checkTableSize(0);

        new MainPage().checkAlert("Spendings succesfully deleted")
                .getStatComponent()
                .checkStatImg(clearStat);

    }

    @User(
            spendings = {
                    @Spending(
                            category = "Обучение",
                            description = "Обучение Advanced 2.0",
                            amount = 79990
                    ),
                    @Spending(
                            category = "Ужин",
                            description = "Ужин в кафе",
                            amount = 5000
                    )
            }
    )
    @ApiLogin
    @Test()
    void checkSpendsTable(@Nonnull UserJson user) throws IOException, InterruptedException {
        Selenide.open(MainPage.URL, MainPage.class)
                .checkThatPageLoaded()
                .getSpendingTable()
                .checkSpendsValues(
                        user.testData().spendings().get(0),
                        user.testData().spendings().get(1)
                );


    }
}
