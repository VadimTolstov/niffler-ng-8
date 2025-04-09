package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import javax.annotation.Nonnull;
import java.util.Optional;

import static guru.qa.niffler.data.Databases.*;

public class SpendDbClient {

    private static final Config CFG = Config.getInstance();

    public @Nonnull SpendJson addSpend(@Nonnull SpendJson spend) {
        return transaction(connection -> {
                    CategoryEntity categoryEntity;
                    SpendEntity spendEntity = SpendEntity.fromEntity(spend);
                    if (spendEntity.getCategory().getId() == null) {
                        // ищем категорию т.к у пользователя не может быть 2 категории с одним названием
                        Optional<CategoryEntity> optionalCategoryEntity = new CategoryDaoJdbc(connection)
                                .findCategoryByUsernameAndCategoryName(spendEntity.getUsername(), spendEntity.getCategory().getName());
                        //если нашли категорию вернем ее если нет создадим новую
                        categoryEntity = optionalCategoryEntity.orElseGet(() -> new CategoryDaoJdbc(connection)
                                .create(spendEntity.getCategory()));
                        spendEntity.setCategory(categoryEntity);
                    }
                    return SpendJson.fromEntity(new SpendDaoJdbc(connection).create(spendEntity));
                },
                CFG.spendJdbcUrl()
        );
    }

    public @Nonnull CategoryJson addCategory(@Nonnull CategoryJson category) {
        return transaction(connection -> {
                    CategoryEntity categoryEntity = CategoryEntity.fromEntity(category);
                    return CategoryJson.fromEntity(new CategoryDaoJdbc(connection).create(categoryEntity));
                },
                CFG.spendJdbcUrl()
        );
    }

    public @Nonnull CategoryJson updateCategory(@Nonnull CategoryJson category) {
        return transaction(connection -> {
                    CategoryEntity categoryEntity = CategoryEntity.fromEntity(category);
                    return CategoryJson.fromEntity(new CategoryDaoJdbc(connection).update(categoryEntity));
                },
                CFG.spendJdbcUrl()
        );
    }
}
