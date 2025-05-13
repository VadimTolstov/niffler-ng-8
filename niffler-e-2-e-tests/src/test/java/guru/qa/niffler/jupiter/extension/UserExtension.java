package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.service.imp.UserApiService;
import guru.qa.niffler.utils.RandomDataUtils;
import io.qameta.allure.Step;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class UserExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);

    private static final String defaultPassword = "12345";
    private final UsersClient usersClient = new UserApiService();


    @Override
    @Step("Создаем пользователе перед началом теста")
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    if ("".equals(userAnno.username())) {
                        final String username = RandomDataUtils.randomUsername();

                        UserJson user = usersClient.createUser(
                                username,
                                defaultPassword
                        );

                        usersClient.createFriends(user, userAnno.friends());
                        usersClient.createIncomeInvitations(user, userAnno.incomeInvitations());
                        usersClient.createOutcomeInvitations(user, userAnno.outcomeInvitations());

                        context.getStore(NAMESPACE).put(
                                context.getUniqueId(),
                                user.withPassword(
                                        defaultPassword
                                )
                        );
                    }
                });
    }


    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return createdUser();
    }

    public static @Nullable UserJson createdUser() {
        final ExtensionContext context = TestMethodContextExtension.context();
        return context.getStore(NAMESPACE).get(context.getUniqueId(), UserJson.class);
    }
}