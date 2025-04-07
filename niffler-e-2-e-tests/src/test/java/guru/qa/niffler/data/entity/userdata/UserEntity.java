package guru.qa.niffler.data.entity.userdata;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements Serializable {

    private UUID id;
    private String username;
    private CurrencyValues currency;
    private String firstname;
    private String surname;
    private String fullname;
    private byte[] photo;
    private byte[] photoSmall;

    public static @Nonnull UserEntity fromEntity(@Nonnull UserJson json) {
        return new UserEntity(
                json.id(),
                json.username(),
                json.currency(),
                json.firstname(),
                json.surname(),
                json.fullname(),
                json.photo() != null ? json.photo().getBytes(StandardCharsets.UTF_8) : null,
                json.photoSmall() != null ? json.photoSmall().getBytes(StandardCharsets.UTF_8) : null
        );
    }
}