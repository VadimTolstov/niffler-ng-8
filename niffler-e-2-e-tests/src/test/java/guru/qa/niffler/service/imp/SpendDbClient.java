package guru.qa.niffler.service.imp;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.hibernate.SpendRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.jdbc.SpendRepositoryJdbc;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

public class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    private final SpendRepository spendRepository = new SpendRepositoryHibernate();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    public @Nonnull SpendJson create(@Nonnull SpendJson spend) {
        return xaTransactionTemplate.execute(() -> {
                    CategoryEntity categoryEntity;
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    if (spendEntity.getCategory().getId() == null) {
                        // ищем категорию т.к у пользователя не может быть 2 категории с одним названием
                        Optional<CategoryEntity> optionalCategoryEntity = spendRepository
                                .findCategoryByUsernameAndSpendName(spendEntity.getUsername(), spendEntity.getCategory().getName());
                        //если нашли категорию вернем ее если нет создадим новую
                        categoryEntity = optionalCategoryEntity.orElseGet(() -> spendRepository.createCategory(spendEntity.getCategory()));
                        spendEntity.setCategory(categoryEntity);
                    }
                    return SpendJson.fromEntity(spendRepository.create(spendEntity));
                }
        );
    }

    @Override
    public SpendJson update(SpendJson spend) {
        return xaTransactionTemplate.execute(() ->
                SpendJson.fromEntity(spendRepository.update(SpendEntity.fromJson(spend)))
        );
    }

    public @Nonnull CategoryJson createCategory(@Nonnull CategoryJson category) {
        return xaTransactionTemplate.execute(() ->
                CategoryJson.fromEntity(spendRepository.createCategory(CategoryEntity.fromJson(category)))
        );
    }

    @Override
    public CategoryJson updateCategory(CategoryJson category) {
        return xaTransactionTemplate.execute(() ->
                CategoryJson.fromEntity(spendRepository.updateCategory(CategoryEntity.fromJson(category)))
        );
    }

    @Override
    public Optional<CategoryJson> findCategoryById(UUID id) {
        return xaTransactionTemplate.execute(() ->
                spendRepository.findCategoryById(id)
                        .map(entity -> Optional.ofNullable(CategoryJson.fromEntity(entity)))
                        .orElse(Optional.empty())
        );
    }

    @Override
    public Optional<CategoryJson> findCategoryByUsernameAndCategoryName(String username, String name) {
        return xaTransactionTemplate.execute(() ->
                spendRepository.findCategoryByUsernameAndSpendName(username, name)
                        .map(category -> Optional.ofNullable(CategoryJson.fromEntity(category)))
                        .orElse(Optional.empty())
        );
    }

    @Override
    public Optional<SpendJson> findById(UUID id) {
        return xaTransactionTemplate.execute(() ->
                spendRepository.findById(id)
                        .map(entity -> Optional.ofNullable(SpendJson.fromEntity(entity)))
                        .orElse(Optional.empty())
        );
    }

    @Override
    public Optional<SpendJson> findByUsernameAndSpendDescription(String username, String description) {
        return xaTransactionTemplate.execute(() ->
                spendRepository.findByUsernameAndSpendDescription(username, description)
                        .map(spend -> Optional.ofNullable(SpendJson.fromEntity(spend)))
                        .orElse(Optional.empty())
        );
    }

    @Override
    public void remove(SpendJson spend) {
        xaTransactionTemplate.execute(() -> {
            spendRepository.remove(SpendEntity.fromJson(spend));
            return null;
        });
    }

    @Override
    public void removeCategory(CategoryJson category) {
        xaTransactionTemplate.execute(() -> {
            spendRepository.removeCategory(CategoryEntity.fromJson(category));
            return null;
        });
    }
}
