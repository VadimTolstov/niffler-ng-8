package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.component.Header;
import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@Getter
@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> {
    protected static final Config CFG = Config.getInstance();

    protected final SelenideElement alert = $("");

    @SuppressWarnings("unchecked")
    public T checkAlert(String message) {
        alert.shouldHave(Condition.text(message));
        return (T) this;
    }

    public abstract T checkThatPageLoaded();

    private final Header headerComponent = new Header();
}