package guru.qa.niffler.service;

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

public class SpendDbClient {

    private final SpendDao spendDao = new SpendDaoJdbc();
    private final CategoryDao categoryDao = new CategoryDaoJdbc();

    public @Nonnull SpendJson addSpend(@Nonnull SpendJson spend) {
        CategoryEntity categoryEntity;
        SpendEntity spendEntity = SpendEntity.fromEntity(spend);
        if (spendEntity.getCategory().getId() == null) {
            // ищем категорию т.к у пользователя не может быть 2 категории с одним названием
            Optional<CategoryEntity> optionalCategoryEntity = categoryDao.findCategoryByUsernameAndCategoryName(spendEntity.getUsername(), spendEntity.getCategory().getName());
            //если нашли категорию вернем ее если нет создадим новую
            categoryEntity = optionalCategoryEntity.orElseGet(() -> categoryDao.create(spendEntity.getCategory()));
            spendEntity.setCategory(categoryEntity);
        }
        return SpendJson.fromEntity(spendDao.create(spendEntity));
    }

    public @Nonnull CategoryJson addCategory(@Nonnull CategoryJson category) {
        CategoryEntity categoryEntity = CategoryEntity.fromEntity(category);
        return CategoryJson.fromEntity(categoryDao.create(categoryEntity));
    }

    public @Nonnull CategoryJson updateCategory(@Nonnull CategoryJson category) {
        CategoryEntity categoryEntity = CategoryEntity.fromEntity(category);
        return CategoryJson.fromEntity(categoryDao.update(categoryEntity));
    }
}
