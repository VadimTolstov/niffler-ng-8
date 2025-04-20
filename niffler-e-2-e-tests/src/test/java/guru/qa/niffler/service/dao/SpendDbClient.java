package guru.qa.niffler.service.dao;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.jdbc.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.SpendDaoJdbc;
import guru.qa.niffler.data.dao.impl.springJdbc.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.springJdbc.SpendDaoSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import javax.annotation.Nonnull;
import java.util.Optional;

public class SpendDbClient {
    private final CategoryDao categoryDaoJdbc = new CategoryDaoJdbc();
    private final SpendDao spendDaoJdbc = new SpendDaoJdbc();

    private final CategoryDao categoryDaoSpringJdbc = new CategoryDaoSpringJdbc();
    private final SpendDao spendDaoSpringJdbc = new SpendDaoSpringJdbc();

    private static final Config CFG = Config.getInstance();


    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    public @Nonnull SpendJson createSpendJdbc(@Nonnull SpendJson spend) {
        return xaTransactionTemplate.execute(() -> {
                    CategoryEntity categoryEntity;
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    if (spendEntity.getCategory().getId() == null) {
                        // ищем категорию т.к у пользователя не может быть 2 категории с одним названием
                        Optional<CategoryEntity> optionalCategoryEntity = categoryDaoJdbc
                                .findCategoryByUsernameAndCategoryName(spendEntity.getUsername(), spendEntity.getCategory().getName());
                        //если нашли категорию вернем ее если нет создадим новую
                        categoryEntity = optionalCategoryEntity.orElseGet(() -> categoryDaoJdbc.create(spendEntity.getCategory()));
                        spendEntity.setCategory(categoryEntity);
                    }
                    return SpendJson.fromEntity(spendDaoJdbc.create(spendEntity));
                }
        );
    }

    public @Nonnull CategoryJson createCategoryJdbc(@Nonnull CategoryJson category) {
        return xaTransactionTemplate.execute(() -> {
                    CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
                    return CategoryJson.fromEntity(categoryDaoJdbc.create(categoryEntity));
                }
        );
    }

    public @Nonnull CategoryJson updateCategoryJdbc(@Nonnull CategoryJson category) {
        return xaTransactionTemplate.execute(() -> {
                    CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
                    return CategoryJson.fromEntity(categoryDaoJdbc.update(categoryEntity));
                }
        );
    }

    public @Nonnull SpendJson createSpendSpringJdbc(@Nonnull SpendJson spend) {
        return xaTransactionTemplate.execute(() -> {
                    CategoryEntity categoryEntity;
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    if (spendEntity.getCategory().getId() == null) {
                        // ищем категорию т.к у пользователя не может быть 2 категории с одним названием
                        Optional<CategoryEntity> optionalCategoryEntity = categoryDaoSpringJdbc
                                .findCategoryByUsernameAndCategoryName(spendEntity.getUsername(), spendEntity.getCategory().getName());
                        //если нашли категорию вернем ее если нет создадим новую
                        categoryEntity = optionalCategoryEntity.orElseGet(() -> categoryDaoSpringJdbc.create(spendEntity.getCategory()));
                        spendEntity.setCategory(categoryEntity);
                    }
                    return SpendJson.fromEntity(spendDaoSpringJdbc.create(spendEntity));
                }
        );
    }

    public @Nonnull CategoryJson creatCategorySpringJdbc(@Nonnull CategoryJson category) {
        return xaTransactionTemplate.execute(() -> {
                    CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
                    return CategoryJson.fromEntity(categoryDaoSpringJdbc.create(categoryEntity));
                }
        );
    }

    public @Nonnull CategoryJson updateCategorySpringJdbc(@Nonnull CategoryJson category) {
        return xaTransactionTemplate.execute(() -> {
                    CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
                    return CategoryJson.fromEntity(categoryDaoSpringJdbc.update(categoryEntity));
                }
        );
    }
}
