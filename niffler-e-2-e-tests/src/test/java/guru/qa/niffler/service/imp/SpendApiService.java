package guru.qa.niffler.service.imp;

import guru.qa.niffler.api.imp.SpendApiClient;
import guru.qa.niffler.ex.ApiException;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendApiService implements SpendClient {

    private final SpendApiClient spendApiClient;

    public SpendApiService() {
        this.spendApiClient = new SpendApiClient();
    }

    @Override
    public @Nonnull SpendJson create(@Nonnull SpendJson spend) {
        return spendApiClient.addSpend(spend);
    }

    @Override
    public @Nonnull SpendJson update(@Nonnull SpendJson spend) {
        return spendApiClient.editSpend(spend);
    }

    @Override
    public @Nonnull CategoryJson createCategory(@Nonnull CategoryJson category) {
        return spendApiClient.addCategory(category);
    }

    @Override
    public @Nonnull CategoryJson updateCategory(@Nonnull CategoryJson category) {
        return spendApiClient.updateCategory(category);
    }

    @Override
    public @Nonnull Optional<CategoryJson> findCategoryById(@Nonnull UUID id) {
        throw new ApiException("Метод находится в разработке");
    }

    @Override
    public @Nonnull Optional<CategoryJson> findCategoryByUsernameAndCategoryName(@Nonnull String username, String name) {
        throw new ApiException("Метод находится в разработке");
    }

    @Override
    public @Nonnull Optional<SpendJson> findById(@Nonnull UUID id) {
        throw new ApiException("Метод находится в разработке");
    }

    @Override
    public @Nonnull Optional<SpendJson> findByUsernameAndSpendDescription(@Nonnull String username, @Nonnull String description) {
        throw new ApiException("Метод находится в разработке");
    }

    @Override
    public void remove(@Nonnull SpendJson spend) {
        spendApiClient.deleteSpends(spend.username(), List.of(spend.id().toString()));
    }

    @Override
    public void removeCategory(@Nonnull CategoryJson category) {
        throw new ApiException("Метод находится в разработке");
    }
}
