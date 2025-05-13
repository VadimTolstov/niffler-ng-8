package guru.qa.niffler.service.imp;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.hibernate.SpendRepositoryHibernate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    private final SpendRepository spendRepository = new SpendRepositoryHibernate();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    public @Nonnull SpendJson create(@Nonnull SpendJson spend) {
        return Objects.requireNonNull(
                xaTransactionTemplate.execute(() -> {
                            CategoryEntity categoryEntity;
                            SpendEntity spendEntity = SpendEntity.fromJson(spend);
                            if (spendEntity.getCategory().getId() == null) {
                                // ищем категорию т.к у пользователя не может быть 2 категории с одним названием
                                Optional<CategoryEntity> optionalCategoryEntity = spendRepository
                                        .findCategoryByUsernameAndSpendName(
                                                spendEntity.getUsername(),
                                                spendEntity.getCategory().getName()
                                        );
                                //если нашли категорию вернем ее если нет создадим новую
                                categoryEntity = optionalCategoryEntity.orElseGet(() ->
                                        spendRepository.createCategory(spendEntity.getCategory()));
                                spendEntity.setCategory(categoryEntity);
                            }
                            return SpendJson.fromEntity(spendRepository.create(spendEntity));
                        }
                ), "Transaction result cannot be null");
    }

    @Override
    public @Nonnull SpendJson update(@Nonnull SpendJson spend) {
        return Objects.requireNonNull(xaTransactionTemplate.execute(() ->
                SpendJson.fromEntity(spendRepository.update(SpendEntity.fromJson(spend)))
        ), "Transaction result cannot be null");
    }

    public @Nonnull CategoryJson createCategory(@Nonnull CategoryJson category) {
        return Objects.requireNonNull(xaTransactionTemplate.execute(() ->
                CategoryJson.fromEntity(spendRepository.createCategory(CategoryEntity.fromJson(category)))
        ), "Transaction result cannot be null");
    }

    @Override
    public @Nonnull CategoryJson updateCategory(@Nonnull CategoryJson category) {
        return Objects.requireNonNull(xaTransactionTemplate.execute(() ->
                CategoryJson.fromEntity(spendRepository.updateCategory(CategoryEntity.fromJson(category)))
        ), "Transaction result cannot be null");
    }

    @Override
    public @Nonnull Optional<CategoryJson> findCategoryById(@Nonnull UUID id) {
        return Objects.requireNonNull(xaTransactionTemplate.execute(() ->
                spendRepository.findCategoryById(id)
                        .map(entity -> Optional.ofNullable(CategoryJson.fromEntity(entity)))
                        .orElse(Optional.empty())
        ),"Transaction result cannot be null");
    }

    @Override
    public @Nonnull Optional<CategoryJson> findCategoryByUsernameAndCategoryName(@Nonnull String username, String name) {
        return Objects.requireNonNull(xaTransactionTemplate.execute(() ->
                spendRepository.findCategoryByUsernameAndSpendName(username, name)
                        .map(category -> Optional.ofNullable(CategoryJson.fromEntity(category)))
                        .orElse(Optional.empty())
        ),"Transaction result cannot be null");
    }

    @Override
    public @Nonnull Optional<SpendJson> findById(@Nonnull UUID id) {
        return Objects.requireNonNull(xaTransactionTemplate.execute(() ->
                spendRepository.findById(id)
                        .map(entity -> Optional.ofNullable(SpendJson.fromEntity(entity)))
                        .orElse(Optional.empty())
        ), "Transaction result cannot be null");
    }

    @Override
    public @Nonnull Optional<SpendJson> findByUsernameAndSpendDescription(@Nonnull String username, @Nonnull String description) {
        return Objects.requireNonNull(xaTransactionTemplate.execute(() ->
                spendRepository.findByUsernameAndSpendDescription(username, description)
                        .map(spend -> Optional.ofNullable(SpendJson.fromEntity(spend)))
                        .orElse(Optional.empty())
        ), "Transaction result cannot be null");
    }

    @Override
    public void remove(@Nonnull SpendJson spend) {
        xaTransactionTemplate.execute(() -> {
            spendRepository.remove(SpendEntity.fromJson(spend));
            return null;
        });
    }

    @Override
    public void removeCategory(@Nonnull CategoryJson category) {
        xaTransactionTemplate.execute(() -> {
            spendRepository.removeCategory(CategoryEntity.fromJson(category));
            return null;
        });
    }
}
