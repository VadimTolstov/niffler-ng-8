package guru.qa.niffler.page;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public class ProfilePage extends BasePage<ProfilePage> {

    public final static String URL = CFG.frontUrl() + "profile";

    private final SelenideElement avatar = $("#image__input").parent().$("img");
    private final SelenideElement userName = $("#username");
    private final SelenideElement nameInput = $("#name");
    private final SelenideElement photoInput = $("input[type='file']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement categoryInput = $("input[name='category']");
    private final SelenideElement archivedSwitcher = $(".MuiSwitch-input");
    private final SelenideElement modalWindowButtonArchived = $("button.MuiButton-containedPrimary:not([id=':rf:'])");

    private final ElementsCollection bubbles = $$(".MuiGrid-root.MuiGrid-item");
    private final ElementsCollection bubblesArchived = $$(".MuiChip-filled.MuiChip-colorDefault");

    @Step("Проверяем, что страница профиля загрузилась")
    @Override
    public ProfilePage checkThatPageLoaded() {
        userName.shouldHave(visible);
        nameInput.shouldHave(visible);
        categoryInput.shouldHave(visible);
        return this;
    }

    @Step("Включить/выключить отображение архивных категорий")
    public ProfilePage clickSwitcherCategories() {
        archivedSwitcher.scrollIntoView(false).click();
        return this;
    }

    @Step("Заполняем поле Username: {0}")
    public ProfilePage usernameInput(String username) {
        userName.setValue(username);
        return this;
    }

    @Step("Заполняем поле Name: {0}")
    public ProfilePage nameInput(String Name) {
        nameInput.setValue(Name);
        return this;
    }

    @Step("Нажимаем кнопку Save changer")
    public ProfilePage clickSaveChanger() {
        submitButton.click();
        return this;
    }

    @Step("Загрузить фото из classpath")
    public ProfilePage uploadPhotoFromClasspath(String path) {
        photoInput.uploadFromClasspath(path);
        return this;
    }

    @Step("Создать категорию: {0}")
    public ProfilePage createCategories(String categoriesName) {
        categoryInput.setValue(categoriesName).pressEnter();
        return this;
    }

    @Step("Проверьте, существует ли фотография")
    public ProfilePage checkPhotoExist() {
        avatar.should(attributeMatching("src", "data:image.*"));
        return this;
    }

    @Step("Проверьте, соответствует ли изображение аватара ожидаемому изображению")
    public ProfilePage checkAvatarImg(BufferedImage expectedAvatar) throws IOException {
        BufferedImage actual = ImageIO.read(Objects.requireNonNull($(".MuiAvatar-img").screenshot()));
        assertFalse(new ScreenDiffResult(
                expectedAvatar,
                actual
        ));
        return this;
    }

    @Step("Проверить фото")
    public ProfilePage checkPhoto(String path) throws IOException {
        final byte[] photoContent;
        try (InputStream is = new ClassPathResource(path).getInputStream()) {
            photoContent = Base64.getEncoder().encode(is.readAllBytes());
        }
        avatar.should(attribute("src", new String(photoContent, StandardCharsets.UTF_8)));
        return this;
    }

    @Step("Проверить userName: {0}")
    public ProfilePage checkUsername(String username) {
        userName.should(value(username));
        return this;
    }

    @Step("Проверить name: {0}")
    public ProfilePage checkName(String name) {
        nameInput.shouldHave(value(name));
        return this;
    }

    @Step("Проверить отображение категорий: {category}")
    public ProfilePage checkCategoryExists(String... categories) {
        // Проверяем, что все переданные тексты присутствуют в коллекции (другие тексты игнорируются)
        for (String category : categories) {
            bubbles.shouldHave(CollectionCondition.anyMatch(
                    "Элемент с текстом '" + category + "' не найден",
                    el -> el.getText().contains(category)
            ));
        }

        // Проверяем видимость каждого элемента с нужным текстом
        for (String category : categories) {
            bubbles.findBy(text(category)).shouldBe(visible);
        }
        return this;
    }

    @Step("Редактирование категории: {category}")
    public ProfilePage editCategory(String categoryName, String newCategoryName) {
        $x("//*[text()='" + categoryName + "']")
                .$x(("../../..//button[@aria-label='Edit category']")).click();

        $("input[placeholder='Edit category']").setValue(newCategoryName).pressEnter();
        return this;
    }

    @Step("Архивирование категории: {category}")
    public ProfilePage archiveCategory(String categoryName) {
        $x("//*[text()='" + categoryName + "']")
                .$x(("../..//button[@aria-label='Archive category']")).click();

        modalWindowButtonArchived.click(ClickOptions.usingJavaScript());
        return this;
    }

    @Step("Разархивирование категории: {category}")
    public ProfilePage unzippingCategory(String categoryName) {
        clickSwitcherCategories();
        $x("//*[text()='" + categoryName + "']")
                .$x("../..//button[@aria-label='Unarchive category']")
                .click();
        modalWindowButtonArchived.click(ClickOptions.usingJavaScript());
        return this;
    }
}
