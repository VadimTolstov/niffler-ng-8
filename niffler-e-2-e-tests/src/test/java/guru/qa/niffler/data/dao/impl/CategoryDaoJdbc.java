package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.ex.DataAccessException;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.sql.*;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class CategoryDaoJdbc implements CategoryDao {
    private static final Config CFG = Config.getInstance();

    @Override
    public @Nonnull CategoryEntity create(@Nonnull CategoryEntity category) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO category(name, username, archived)" +
                            "VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                ps.setString(1, category.getName());
                ps.setString(2, category.getUsername());
                ps.setBoolean(3, category.isArchived());

                ps.executeUpdate();

                final UUID generatedKey;
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedKey = rs.getObject("id", UUID.class);
                    } else {
                        throw new SQLException("Can't find id in ResultSet");
                    }
                }
                category.setId(generatedKey);
                return category;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при создании категории", e);
        }
    }


    @Override
    public @Nonnull Optional<CategoryEntity> findCategoryById(@Nonnull UUID id) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM category WHERE id = ?"
            )) {
                ps.setObject(1, id);
                ps.execute();

                try (ResultSet rs = ps.getResultSet()) {
                    if (rs.next()) {
                        CategoryEntity ce = new CategoryEntity(
                                rs.getObject("id", UUID.class),
                                rs.getString("name"),
                                rs.getString("username"),
                                rs.getBoolean("archived")
                        );
                        return Optional.ofNullable(ce);
                    } else {
                        return Optional.empty();
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при поиске категории по id = " + id, e);
        }
    }

    @Override
    public @Nonnull Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(@Nonnull String username, @Nonnull String categoryName) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM category WHERE username = ? and name = ?"
            )) {
                ps.setString(1, username);
                ps.setString(2, categoryName);
                ps.execute();

                try (ResultSet rs = ps.getResultSet()) {
                    if (rs.next()) {
                        CategoryEntity ce = new CategoryEntity(
                                rs.getObject("id", UUID.class),
                                rs.getString("name"),
                                rs.getString("username"),
                                rs.getBoolean("archived")
                        );
                        return Optional.ofNullable(ce);
                    } else {
                        return Optional.empty();
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при поиске категории по username = " + username + " и categoryName = " + categoryName, e);
        }
    }

    @Override
    public @Nonnull CategoryEntity update(@Nonnull CategoryEntity category) {
        if (category.getId() == null) {
            throw new DataAccessException("При обновлении Category в CategoryEntity id не должен быть null");
        }
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "UPDATE category SET name = ?, username = ?, archived = ? WHERE id = ?"
            )) {
                ps.setString(1, category.getName());
                ps.setString(2, category.getUsername());
                ps.setBoolean(3, category.isArchived());
                ps.setObject(4, category.getId());

                // Проверяем количество обновленных строк
                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new DataAccessException("Категория с id " + category.getId() + " не найдена");
                }

                return category;
            }
        } catch (SQLException e) {
            log.error("Ошибка при обновлении категории с id {}", category.getId(), e);
            throw new DataAccessException("Ошибка при обновлении категории", e);
        }
    }

    @Override
    public void delete(@Nonnull UUID id) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM category WHERE id = ?"
            )) {
                ps.setObject(1, id);

                // Проверяем количество обновленных строк
                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new DataAccessException("Категория с id " + id + " не найдена");
                }
            }
        } catch (SQLException e) {
            log.error("Ошибка при удалении категории с id {}", id, e);
            throw new DataAccessException("Ошибка при удалении категории с id " + id, e);
        }
    }
}