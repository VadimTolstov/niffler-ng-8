package guru.qa.niffler.data.repository.impl.spring;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.springJdbc.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.springJdbc.SpendDaoSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendRepositorySpringJdbc implements SpendRepository {

    private final SpendDao spendDao = new SpendDaoSpringJdbc();
    private final CategoryDao categoryDao = new CategoryDaoSpringJdbc();

    @Override
    public @Nonnull SpendEntity create(@Nonnull SpendEntity spend) {
        spendDao.create(spend);
        return spend;
    }

    @Override
    public @Nonnull Optional<SpendEntity> findById(@Nonnull UUID id) {
        SpendEntity spend;
        Optional<SpendEntity> spendOptional = spendDao.findById(id);
        if (spendOptional.isPresent()) {
            spend = spendOptional.get();
            Optional<CategoryEntity> categoryOptional = categoryDao.findById(spend.getCategory().getId());
            if (categoryOptional.isPresent()) {
                spend.setCategory(categoryOptional.get());
                return Optional.ofNullable(spend);
            }
            return Optional.ofNullable(spend);
        }
        return Optional.empty();
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
    public void removeCategory(CategoryEntity category) {
        List<SpendEntity> spendEntityList = spendDao.findByCategoryId(category.getId());
        for (SpendEntity spend : spendEntityList) {
            spendDao.delete(spend.getId());
        }
        categoryDao.delete(category.getId());
    }

    @Override
    public @Nonnull SpendEntity update(@Nonnull SpendEntity spend) {
        return spendDao.update(spend);

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
}