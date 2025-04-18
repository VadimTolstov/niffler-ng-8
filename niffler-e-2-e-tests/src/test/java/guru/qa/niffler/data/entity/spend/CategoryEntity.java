package guru.qa.niffler.data.entity.spend;

import guru.qa.niffler.model.CategoryJson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryEntity implements Serializable {
    private UUID id;
    private String name;
    private String username;
    private boolean archived;

    public CategoryEntity(UUID id) {
        this.id = id;
    }

    public static @Nonnull CategoryEntity fromJson(@Nonnull CategoryJson json) {
        return new CategoryEntity(
                json.id(),
                json.name(),
                json.username(),
                json.archived()
        );
    }
}
