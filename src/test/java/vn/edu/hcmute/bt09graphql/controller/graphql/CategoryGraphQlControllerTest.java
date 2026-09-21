package vn.edu.hcmute.bt09graphql.controller.graphql;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.GraphQlTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import vn.edu.hcmute.bt09graphql.config.GraphQlConfig;
import vn.edu.hcmute.bt09graphql.config.GraphQlExceptionResolver;
import vn.edu.hcmute.bt09graphql.dto.request.CategoryRequest;
import vn.edu.hcmute.bt09graphql.dto.response.CategoryResponse;
import vn.edu.hcmute.bt09graphql.service.CategoryService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@GraphQlTest(CategoryGraphQlController.class)
@Import({GraphQlConfig.class, GraphQlExceptionResolver.class})
class CategoryGraphQlControllerTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void categories_queryReturnsList() {
        CategoryResponse cat1 = new CategoryResponse(1L, "Electronics", "elec.jpg");
        when(categoryService.findAll()).thenReturn(List.of(cat1));

        String document = """
            query {
                categories {
                    id
                    name
                    images
                }
            }
        """;

        graphQlTester.document(document)
                .execute()
                .path("categories[0].id").entity(String.class).isEqualTo("1")
                .path("categories[0].name").entity(String.class).isEqualTo("Electronics")
                .path("categories[0].images").entity(String.class).isEqualTo("elec.jpg");
    }

    @Test
    void category_queryByIdReturnsCategory() {
        CategoryResponse cat1 = new CategoryResponse(1L, "Electronics", "elec.jpg");
        when(categoryService.findById(1L)).thenReturn(cat1);

        String document = """
            query {
                category(id: "1") {
                    id
                    name
                }
            }
        """;

        graphQlTester.document(document)
                .execute()
                .path("category.id").entity(String.class).isEqualTo("1")
                .path("category.name").entity(String.class).isEqualTo("Electronics");
    }

    @Test
    void createCategory_mutationReturnsCreatedCategory() {
        CategoryResponse cat = new CategoryResponse(2L, "Books", "books.jpg");
        when(categoryService.create(any(CategoryRequest.class))).thenReturn(cat);

        String document = """
            mutation {
                createCategory(input: {name: "Books", images: "books.jpg"}) {
                    id
                    name
                    images
                }
            }
        """;

        graphQlTester.document(document)
                .execute()
                .path("createCategory.id").entity(String.class).isEqualTo("2")
                .path("createCategory.name").entity(String.class).isEqualTo("Books");

        verify(categoryService).create(any(CategoryRequest.class));
    }

    @Test
    void updateCategory_mutationReturnsUpdatedCategory() {
        CategoryResponse updated = new CategoryResponse(1L, "New Electronics", "new.jpg");
        when(categoryService.update(eq(1L), any(CategoryRequest.class))).thenReturn(updated);

        String document = """
            mutation {
                updateCategory(id: "1", input: {name: "New Electronics", images: "new.jpg"}) {
                    id
                    name
                }
            }
        """;

        graphQlTester.document(document)
                .execute()
                .path("updateCategory.id").entity(String.class).isEqualTo("1")
                .path("updateCategory.name").entity(String.class).isEqualTo("New Electronics");
    }

    @Test
    void deleteCategory_mutationReturnsBoolean() {
        when(categoryService.delete(1L)).thenReturn(true);

        String document = """
            mutation {
                deleteCategory(id: "1")
            }
        """;

        graphQlTester.document(document)
                .execute()
                .path("deleteCategory").entity(Boolean.class).isEqualTo(true);

        verify(categoryService).delete(1L);
    }

    @Test
    void deleteCategory_withAssociatedProducts_returnsGraphQLError() {
        when(categoryService.delete(1L)).thenThrow(new IllegalStateException("Cannot delete category with id 1 because it still has associated products"));

        String document = """
            mutation {
                deleteCategory(id: "1")
            }
        """;

        graphQlTester.document(document)
                .execute()
                .errors()
                .satisfy(errors -> {
                    org.junit.jupiter.api.Assertions.assertFalse(errors.isEmpty());
                    org.junit.jupiter.api.Assertions.assertTrue(
                            errors.get(0).getMessage().contains("Cannot delete category with id 1 because it still has associated products")
                    );
                });
    }
}
