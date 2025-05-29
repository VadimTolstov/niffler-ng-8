package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.io.IOException;

@WebTest
@ParametersAreNonnullByDefault
public class ProfileTest {

    @User
    @Test
    @DisplayName("Редактируем поле Name в профиле пользователя")
    void editingNameFieldInProfile(UserJson user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .doLogin(new MainPage(), user.username(), "12345")
                .getHeaderComponent()
                .toProfilePage()
                .nameInput("HTTPs")
                .clickSaveChanger()
                .checkAlert("Profile successfully updated");
    }

    @User(
            categories = @Category(
                    archived = false
            )
    )
    @Test
    @DisplayName("Перевод категории в архивную")
    void categoryToAnArchived(UserJson user) {
        final String categoriesName = user.testData().categories().getFirst().name();
        Selenide.open(LoginPage.URL, LoginPage.class)
                .doLogin(new MainPage(), user.username(), "12345")
                .getHeaderComponent()
                .toProfilePage()
                .checkCategoryExists(categoriesName)
                .archiveCategory(categoriesName)
                .clickSwitcherCategories()
                .checkCategoryExists(categoriesName);
    }

    @User(
            categories = @Category(
                    name = "archived3",
                    archived = true
            )
    )
    @Test
    @DisplayName("Перевод категории из архивной в действующий")
    void categoryFromAnArchivedOneToAnActive(UserJson user) {
        final CategoryJson archivedCategory = user.testData().categories().getFirst();

        Selenide.open(LoginPage.URL, LoginPage.class)
                .doLogin(new MainPage(), user.username(), user.testData().password())
                .getHeaderComponent()
                .toProfilePage()
                .unzippingCategory(archivedCategory.name())
                .checkCategoryExists(archivedCategory.name());
    }

    @User(
            categories = @Category(
                    archived = true
            )
    )
    @Test
    @DisplayName("Перевод категории из архивной в действующий с изменением названия")
    void categoryFromAnArchivedOneToAnActiveAndNewName(UserJson user) {
        final String newNameCategory = RandomDataUtils.randomCategoryName();

        Selenide.open(LoginPage.URL, LoginPage.class)
                .doLogin(new MainPage(), user.username(), user.testData().password())
                .getHeaderComponent()
                .toProfilePage()
                .unzippingCategory(user.testData().categories().getFirst().name())
                .checkCategoryExists(user.testData().categories().getFirst().name())
                .clickSwitcherCategories()
                .editCategory(user.testData().categories().getFirst().name(), newNameCategory)
                .checkCategoryExists(newNameCategory);
    }

    @User
    @ScreenShotTest(value = "img/expectedAvatar.png")
    @DisplayName("Перевод категории из архивной в действующий с изменением названия")
    void avatarTest(@Nonnull UserJson user, BufferedImage expectedAvatar) throws IOException {
        ProfilePage profilePage = Selenide.open(LoginPage.URL, LoginPage.class)
                .doLogin(new MainPage(), user.username(), user.testData().password())
                .getHeaderComponent()
                .toProfilePage()
                .uploadPhotoFromClasspath("img/cat.png")
                .clickSaveChanger();
        Selenide.refresh();

        profilePage.checkPhotoExist()
                .checkAvatarImg(expectedAvatar);
    }
}
