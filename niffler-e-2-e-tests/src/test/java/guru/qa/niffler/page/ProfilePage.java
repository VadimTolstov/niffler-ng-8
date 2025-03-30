package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProfilePage extends BasePage<ProfilePage> {

    public final static String URL = CFG.frontUrl() + "profile";

    private final SelenideElement avatar = $("#image__input").parent().$("img");
    private final SelenideElement userName = $("#username");
    private final SelenideElement nameInput = $("#name");
    private final SelenideElement photoInput = $("input[type='file']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement categoryInput = $("input[name='category']");
    private final SelenideElement archivedSwitcher = $(".MuiSwitch-input");

    private final ElementsCollection bubbles = $$(".MuiChip-filled.MuiChip-colorPrimary");
    private final ElementsCollection bubblesArchived = $$(".MuiChip-filled.MuiChip-colorDefault");

    @Override
    public ProfilePage checkThatPageLoaded() {



        return this;
    }
}
