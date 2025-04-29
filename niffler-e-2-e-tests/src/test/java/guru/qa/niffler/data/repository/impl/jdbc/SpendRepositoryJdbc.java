package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.jdbc.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendRepositoryJdbc implements SpendRepository {

    private final SpendDao spendDao = new SpendDaoJdbc();
    private final CategoryDao categoryDao = new CategoryDaoJdbc();

    @Override
    public SpendEntity create(SpendEntity spend) {
        return spendDao.create(spend);
    }

    @Override
    public SpendEntity update(SpendEntity spend) {
        return spendDao.update(spend);
    }

    @Override
    public Optional<SpendEntity> findById(UUID id) {
        return spendDao.findById(id)
                .map(spend -> {
                    Optional.ofNullable(spend.getCategory())
                            .map(CategoryEntity::getId)
                            .flatMap(categoryDao::findById)
                            .ifPresent(spend::setCategory);
                    return spend;
                });
    }

    @Override
    public Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
        return spendDao.findByUsernameAndSpendDescription(username, description);
    }

    @Override
    public void remove(SpendEntity spend) {
        spendDao.delete(spend.getId());
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        return categoryDao.findCategoryByUsernameAndCategoryName(
                category.getUsername(),
                category.getName()
        ).orElseGet(() -> categoryDao.create(category));
    }

    @Override
    public CategoryEntity updateCategory(CategoryEntity category) {
        return categoryDao.update(category);
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return categoryDao.findById(id);
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndSpendName(String username, String name) {
        return categoryDao.findCategoryByUsernameAndCategoryName(username, name);
    }

    @Override
    public void removeCategory(CategoryEntity category) {
        List<SpendEntity> spendEntityList = spendDao.findByCategoryId(category.getId());
        for (SpendEntity spend : spendEntityList) {
            spendDao.delete(spend.getId());
        }
        categoryDao.delete(category.getId());
    }
}
