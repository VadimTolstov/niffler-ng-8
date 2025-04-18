package guru.qa.niffler.data.entity.spend;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SpendEntity implements Serializable {
    private UUID id;
    private String username;
    private CurrencyValues currency;
    private Date spendDate;
    private Double amount;
    private String description;
    private CategoryEntity category;

    public static @Nonnull SpendEntity fromJson(@Nonnull SpendJson json) {
        return new SpendEntity(
                json.id(),
                json.username(),
                json.currency(),
                new java.sql.Date(json.spendDate().getTime()),
                json.amount(),
                json.description(),
                CategoryEntity.fromJson(json.category())
        );
    }
}
