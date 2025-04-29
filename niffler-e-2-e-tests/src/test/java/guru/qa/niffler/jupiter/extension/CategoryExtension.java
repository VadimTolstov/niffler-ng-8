package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.hibernate.SpendRepositoryHibernate;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.service.imp.SpendDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Optional;

public class CategoryExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
    private final SpendRepository spendRepository = new SpendRepositoryHibernate();


    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    if (ArrayUtils.isNotEmpty(userAnno.categories())) {
                        Category categoryAnno = userAnno.categories()[0];
                        final String nameCategory = StringUtils.isEmpty(categoryAnno.name())
                                ? RandomDataUtils.randomCategoryName()
                                : categoryAnno.name();

                        CategoryJson categoryJson = new CategoryJson(
                                null,
                                nameCategory,
                                userAnno.username(),
                                false
                        );
                        CategoryJson create = CategoryJson.fromEntity(spendRepository.createCategory(CategoryEntity.fromJson(categoryJson)));
                        //не убираю updateCategory т.к при переходе на API он снова понадобится
                        if (categoryAnno.archived()) {
                            CategoryJson archivedCategory = new CategoryJson(
                                    create.id(),
                                    create.name(),
                                    create.username(),
                                    true
                            );
                         //   create = spendRepository.u(archivedCategory);
                        }

                        context.getStore(NAMESPACE).put(context.getUniqueId(), create);
                    }
                });

    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
//        Optional.ofNullable(context.getStore(CategoryExtension.NAMESPACE).get(context.getUniqueId(), CategoryJson.class))
//                .ifPresent(categoryJson -> {
//                  //  spendDbClient.updateCategoryJdbc(
//                            new CategoryJson(
//                                    categoryJson.id(),
//                                    categoryJson.name(),
//                                    categoryJson.username(),
//                                    true
//                            ));
//                });
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