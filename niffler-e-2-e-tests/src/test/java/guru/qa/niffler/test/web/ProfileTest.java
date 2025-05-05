package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.annotation.ParametersAreNonnullByDefault;

@WebTest
@ParametersAreNonnullByDefault
public class ProfileTest {

    @User(
            username = "duck",
            categories = @Category(
                    archived = false
            )
    )
    @Test
    @DisplayName("Перевод категории в архивную")
    void categoryToAnArchived(CategoryJson[] category) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .doLogin(new MainPage(), "duck", "12345")
                .getHeaderComponent()
                .toProfilePage()
                .checkCategoryExists(category[0].name())
                .archiveCategory(category[0].name())
                .clickSwitcherCategories()
                .checkCategoryExists(category[0].name());
    }

    @User(
              username = "duck",
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
            username = "duck",
            categories = @Category(
                    archived = true
            )
    )
    @Test
    @DisplayName("Перевод категории из архивной в действующий с изменением названия")
    void categoryFromAnArchivedOneToAnActiveAndNewName(CategoryJson category) {
        final String newNameCategory = RandomDataUtils.randomCategoryName();

        Selenide.open(LoginPage.URL, LoginPage.class)
                .doLogin(new MainPage(), category.username(), "12345")
                .getHeaderComponent()
                .toProfilePage()
                .unzippingCategory(category.name())
                .checkCategoryExists(category.name())
                .clickSwitcherCategories()
                .editCategory(category.name(), newNameCategory)
                .checkCategoryExists(newNameCategory);
    }
}
