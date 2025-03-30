package guru.qa.niffler.jupiter;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.utils.RandomDataUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class CategoryExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
    private final SpendApiClient spendApiClient = new SpendApiClient();


    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Category.class)
                .ifPresent(anno -> {
                    final String nameCategory = StringUtils.isEmpty(anno.name())
                            ? RandomDataUtils.randomCategoryName()
                            : anno.name();

                    CategoryJson categoryJson = new CategoryJson(
                            null,
                            nameCategory,
                            anno.username(),
                            false
                    );
                    CategoryJson create = spendApiClient.addCategory(categoryJson);
                    if (anno.archived()) {
                        CategoryJson archivedCategory = new CategoryJson(
                                create.id(),
                                create.name(),
                                create.username(),
                                true
                        );
                        create = spendApiClient.updateCategory(archivedCategory);
                    }

                    context.getStore(NAMESPACE).put(context.getUniqueId(), create);
                });

    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        CategoryJson categoryJson = context.getStore(CategoryExtension.NAMESPACE).get(context.getUniqueId(), CategoryJson.class);
        spendApiClient.updateCategory(
                new CategoryJson(
                        categoryJson.id(),
                        categoryJson.name(),
                        categoryJson.username(),
                        true
                ));
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), CategoryJson.class);
    }


}
