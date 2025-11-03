package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.io.IOException;

@WebTest
@ParametersAreNonnullByDefault
public class ProfileTest {

    @User
    @ApiLogin
    @Test
    @DisplayName("Редактируем поле Name в профиле пользователя")
    void editingNameFieldInProfile(UserJson user) {
        Selenide.open(ProfilePage.URL, ProfilePage.class)
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
    @ApiLogin
    @DisplayName("Перевод категории в архивную")
    void categoryToAnArchived(UserJson user) {
        final String categoriesName = user.testData().categories().getFirst().name();
        Selenide.open(ProfilePage.URL, ProfilePage.class)
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
    @ApiLogin
    @Test
    @DisplayName("Перевод категории из архивной в действующий")
    void categoryFromAnArchivedOneToAnActive(UserJson user) {
        final CategoryJson archivedCategory = user.testData().categories().getFirst();
        Selenide.open(ProfilePage.URL, ProfilePage.class)
                .unzippingCategory(archivedCategory.name())
                .checkCategoryExists(archivedCategory.name());
    }

    @User(
            categories = @Category(
                    archived = true
            )
    )
    @ApiLogin
    @Test
    @DisplayName("Перевод категории из архивной в действующий с изменением названия")
    void categoryFromAnArchivedOneToAnActiveAndNewName(UserJson user) {
        final String newNameCategory = RandomDataUtils.randomCategoryName();

        Selenide.open(ProfilePage.URL, ProfilePage.class)
                .unzippingCategory(user.testData().categories().getFirst().name())
                .checkCategoryExists(user.testData().categories().getFirst().name())
                .clickSwitcherCategories()
                .editCategory(user.testData().categories().getFirst().name(), newNameCategory)
                .checkCategoryExists(newNameCategory);
    }

    @User
    @ApiLogin
    @ScreenShotTest(expected = "expectedAvatar.png", rewriteExpected = true)
    @DisplayName("Перевод категории из архивной в действующий с изменением названия")
    void avatarTest(BufferedImage expectedAvatar) throws IOException {
        ProfilePage profilePage = Selenide.open(ProfilePage.URL, ProfilePage.class)
                .uploadPhotoFromClasspath("img/cat.png")
                .clickSaveChanger();
        Selenide.refresh();

        profilePage.checkPhotoExist()
                .checkAvatarImg(expectedAvatar);
    }
}
