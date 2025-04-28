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
    public CategoryEntity createCategory(CategoryEntity category) {
        return categoryDao.create(category);
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
