package guru.qa.niffler.page.component;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.condition.Bubble;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.jupiter.extension.ScreenShotTestExtension;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;
import static guru.qa.niffler.condition.ScreenshotConditions.image;
import static guru.qa.niffler.condition.StatConditions.color;
import static guru.qa.niffler.condition.StatConditions.statBubblesInAnyOrder;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public class StatComponent extends BaseComponent<StatComponent> {
    public StatComponent() {
        super($("#stat"));
    }

    private final ElementsCollection bubbles = self.$("#legend-container").$$("li");
    private final SelenideElement chart = $("canvas[role='img']");

    @Step("Проверяем цвет траты {expectedColors}")
    @Nonnull
    public StatComponent checkBubbles(Color... expectedColors) {
        bubbles.should(color(expectedColors));
        return this;
    }

    @Step("Проверяем цвет и текст траты {expectedColors}")
    @Nonnull
    public StatComponent checkBubblesAndText(Bubble... expectedBubbles) {
        bubbles.should(statBubblesInAnyOrder(expectedBubbles));
        return this;
    }

    @Step("Убедитесь, что статистическое изображение соответствует ожидаемому")
    @Nonnull
    public StatComponent checkStatImg(BufferedImage expected) throws IOException {
        // Сначала делаем скриншот и сохраняем его
        BufferedImage actual = ImageIO.read(requireNonNull(
                Screenshots.takeScreenShot(chart)
        ));
        ScreenShotTestExtension.setActual(actual);

        chart.shouldHave(image(expected));
        return this;
    }

    @Step("Убедитесь, что в статистической ячейке есть текст {text}")
    @Nonnull
    public StatComponent checkStatText(String... text) {
        for (String s : text) {
            bubbles.findBy(Condition.exactText(s)).shouldBe(Condition.visible);
        }
        return this;
    }
}
