package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.condition.Bubble;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.io.IOException;

@WebTest
@ParametersAreNonnullByDefault
public class SpendingTest {

    private static final Config CFG = Config.getInstance();

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Niffler 2.0",
                    amount = 89000.00,
                    currency = CurrencyValues.RUB
            )
    )
    @Test
    void spendingDescriptionShouldBeUpdatedByTableAction(UserJson user) {
        final String newDescription = "Обучение Niffler NG";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(new MainPage(), "duck", "12345")
                .checkThatPageLoaded()
                .getHeaderComponent()
                .addSpendingPage()
                .editDescription(user.testData().spendings().getFirst().description())
                .editDescription(newDescription)
                .getSpendingTable()
                .checkTableContains(newDescription);

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
    @ScreenShotTest(value = "img/expected-stat.png")
    void checkStatComponentTest(@Nonnull UserJson user, BufferedImage expected) throws IOException, InterruptedException {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .doLogin(new MainPage(), user.username(), user.testData().password())
                .checkThatPageLoaded()
                .getStatComponent()
                .checkStatImg(expected)
                .checkStatText("Обучение 79990 ₽")
                .checkBubblesAndText(
                        new Bubble(Color.yellow, "Обучение 79990 ₽"),
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
    @ScreenShotTest(value = "img/clear-stat.png", rewriteExpected = true)
    void deleteSpendingTest(@Nonnull UserJson user, BufferedImage clearStat) throws IOException {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .doLogin(new MainPage(), user.username(), user.testData().password())
                .checkThatPageLoaded()
                .getSpendingTable()
                .checkTableContains("Обучение Advanced 2.0")
                .deleteSpending("Обучение Advanced 2.0")
                .checkTableSize(0);

        new MainPage().getStatComponent()
                .checkStatImg(clearStat)
                .checkStatText("");
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

    @Test()
    void checkSpendsTable(@Nonnull UserJson user) throws IOException, InterruptedException {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .doLogin(new MainPage(), user.username(), user.testData().password())
                .checkThatPageLoaded()
                .getSpendingTable()
                .checkSpendsValues(
                        user.testData().spendings().get(0),
                                       user.testData().spendings().get(1)
                );


    }
}
