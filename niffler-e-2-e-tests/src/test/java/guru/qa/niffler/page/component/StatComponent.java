package guru.qa.niffler.page.component;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;
import static guru.qa.niffler.component.StatConditions.color;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public class StatComponent extends BaseComponent<StatComponent> {
    public StatComponent() {
        super($("#stat"));
    }

    private final ElementsCollection bubbles = self.$("#legend-container").$$("li");
    private final SelenideElement chart = $("canvas[role='img']");
    private final SelenideElement statCell = self.$("#legend-container");

    @Step("Проверяем цвет траты {expectedColors}")
    @Nonnull
    public StatComponent checkBubbles(Color... expectedColors) {
        bubbles.should(color(expectedColors));
        return this;
    }

    @Step("Убедитесь, что статистическое изображение соответствует ожидаемому")
    @Nonnull
    public StatComponent checkStatImg(BufferedImage expected) throws IOException {
        sleep(3000);
        BufferedImage actual = ImageIO.read(Objects.requireNonNull(chart.screenshot()));
        assertFalse(new ScreenDiffResult(
                        expected, actual),
                "Screen comparison failure"
        );
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
