package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class EditSpendingPage {

    private final SelenideElement descriptionInput = $("#description");
    private final SelenideElement submitBtn = $("#save");

    public void editDescription(String description) {
        descriptionInput.clear();
        descriptionInput.setValue(description);
        submitBtn.click();
    }
}