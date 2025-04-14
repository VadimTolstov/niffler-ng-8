package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.ex.DataAccessException;
import guru.qa.niffler.model.CurrencyValues;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.sql.*;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class SpendDaoJdbc implements SpendDao {

    private final Connection connection;

    public SpendDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public @Nonnull SpendEntity create(@Nonnull SpendEntity spend) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO spend(username, spend_date, currency, amount, description, category_id)" +
                        "VALUES (?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, spend.getUsername());
            ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
            ps.setString(3, spend.getCurrency().name());
            ps.setDouble(4, spend.getAmount());
            ps.setString(5, spend.getDescription());
            ps.setObject(6, spend.getCategory().getId());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can't find id in ResultSet");
                }
            }
            spend.setId(generatedKey);
            return spend;
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при создании траты", e);
        }
    }

    @Override
    public @Nonnull Optional<SpendEntity> findSpendById(@Nonnull UUID id) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM spend WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    UUID categoryId = rs.getObject("category_id", UUID.class);
                    Optional<CategoryEntity> category = new CategoryDaoJdbc(connection).findCategoryById(categoryId);

                    if (category.isEmpty()) {
                        log.warn("Категория с id {} не найдена для траты {}", categoryId, id);
                        return Optional.empty();
                    }

                    SpendEntity se = new SpendEntity(
                            rs.getObject("id", UUID.class),
                            rs.getString("username"),
                            CurrencyValues.valueOf(rs.getString("currency")),
                            new java.util.Date(rs.getDate("spend_date").getTime()),
                            rs.getDouble("amount"),
                            rs.getString("description"),
                            category.get()
                    );
                    return Optional.ofNullable(se);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при поиске траты по id = " + id, e);
        }
    }

    @Override
    public @Nonnull SpendEntity update(@Nonnull SpendEntity spend) {
        if (spend.getId() == null) {
            throw new DataAccessException("При обновлении Spend в SpendEntity id не должен быть null");
        }
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE spend SET username = ?, spend_date = ?, currency = ?, amount = ?," +
                        " description = ?, category_id = ? WHERE id = ?"
        )) {
            ps.setString(1, spend.getUsername());
            ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
            ps.setString(3, spend.getCurrency().name());
            ps.setDouble(4, spend.getAmount());
            ps.setString(5, spend.getDescription());
            ps.setObject(6, spend.getCategory().getId());
            ps.setObject(7, spend.getId());

            // Проверяем количество обновленных строк
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Трата с id " + spend.getId() + " не найдена");
            }

            return spend;
        } catch (SQLException e) {
            log.error("Ошибка при обновлении траты с id {}", spend.getId(), e);
            throw new DataAccessException("Ошибка при обновлении траты", e);
        }
    }

    @Override
    public void delete(@Nonnull UUID id) {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM spend WHERE id = ?"
        )) {
            ps.setObject(1, id);

            // Проверяем количество обновленных строк
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Траты с id " + id + " не найдена");
            }
        } catch (SQLException e) {
            log.error("Ошибка при удалении траты с id {}", id, e);
            throw new DataAccessException("Ошибка при удалении траты с id " + id, e);
        }
    }
}
