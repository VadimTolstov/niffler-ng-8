package guru.qa.niffler.data.entity.auth;

import guru.qa.niffler.model.Authority;
import guru.qa.niffler.model.AuthorityJson;
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
public class AuthorityEntity implements Serializable {

    private UUID id;
    private AuthUserEntity userId;
    private Authority authority;

    public static @Nonnull AuthorityEntity fromJson(@Nonnull AuthorityJson authority) {
        return new AuthorityEntity(
                authority.id(),
                AuthUserEntity.fromJson(authority.userId()),
                authority.authority()
        );
    }
}
