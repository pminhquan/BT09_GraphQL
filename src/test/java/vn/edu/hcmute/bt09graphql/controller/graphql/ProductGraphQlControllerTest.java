package vn.edu.hcmute.bt09graphql.controller.graphql;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.GraphQlTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import vn.edu.hcmute.bt09graphql.config.GraphQlConfig;
import vn.edu.hcmute.bt09graphql.config.GraphQlExceptionResolver;
import vn.edu.hcmute.bt09graphql.dto.request.ProductRequest;
import vn.edu.hcmute.bt09graphql.dto.response.ProductResponse;
import vn.edu.hcmute.bt09graphql.service.ProductService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@GraphQlTest(ProductGraphQlController.class)
@Import({GraphQlConfig.class, GraphQlExceptionResolver.class})
class ProductGraphQlControllerTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private ProductService productService;

    @Test
    void products_queryReturnsList() {
        ProductResponse p1 = new ProductResponse(1L, "Mouse", 10, "Optical mouse", new BigDecimal("25.50"), 1L, 2L, "Electronics", "Alice");
        when(productService.findAll()).thenReturn(List.of(p1));

        String document = """
            query {
                products {
                    id
                    title
                    price
                    quantity
                    categoryName
                    userFullname
                }
            }
        """;

        graphQlTester.document(document)
                .execute()
                .path("products[0].id").entity(String.class).isEqualTo("1")
                .path("products[0].title").entity(String.class).isEqualTo("Mouse")
                .path("products[0].price").entity(BigDecimal.class).isEqualTo(new BigDecimal("25.50"))
                .path("products[0].quantity").entity(Integer.class).isEqualTo(10)
                .path("products[0].categoryName").entity(String.class).isEqualTo("Electronics")
                .path("products[0].userFullname").entity(String.class).isEqualTo("Alice");
    }

    @Test
    void productsByPriceAsc_queryReturnsOrderedListWithBigDecimalPrice() {
        ProductResponse p1 = new ProductResponse(1L, "Mouse", 10, "Optical mouse", new BigDecimal("25.50"), 1L, 2L, "Electronics", "Alice");
        ProductResponse p2 = new ProductResponse(2L, "Keyboard", 5, "Mechanical", new BigDecimal("75.00"), 1L, 2L, "Electronics", "Alice");
        when(productService.findAllOrderByPriceAsc()).thenReturn(List.of(p1, p2));

        String document = """
            query {
                productsByPriceAsc {
                    id
                    title
                    price
                }
            }
        """;

        graphQlTester.document(document)
                .execute()
                .path("productsByPriceAsc[0].id").entity(String.class).isEqualTo("1")
                .path("productsByPriceAsc[0].price").entity(BigDecimal.class).isEqualTo(new BigDecimal("25.50"))
                .path("productsByPriceAsc[1].id").entity(String.class).isEqualTo("2")
                .path("productsByPriceAsc[1].price").entity(BigDecimal.class).isEqualTo(new BigDecimal("75.00"));
    }

    @Test
    void productsByCategory_queryReturnsCategoryProducts() {
        ProductResponse p1 = new ProductResponse(1L, "Mouse", 10, "Optical mouse", new BigDecimal("25.50"), 1L, 2L, "Electronics", "Alice");
        when(productService.findByCategoryId(2L)).thenReturn(List.of(p1));

        String document = """
            query {
                productsByCategory(categoryId: "2") {
                    id
                    title
                    categoryId
                }
            }
        """;

        graphQlTester.document(document)
                .execute()
                .path("productsByCategory[0].id").entity(String.class).isEqualTo("1")
                .path("productsByCategory[0].title").entity(String.class).isEqualTo("Mouse");
    }

    @Test
    void createProduct_mutationAcceptsBigDecimalPriceAndReturnsProduct() {
        ProductResponse created = new ProductResponse(3L, "Laptop", 3, "High-end", new BigDecimal("1499.99"), 1L, 2L, "Electronics", "Alice");
        when(productService.create(any(ProductRequest.class))).thenReturn(created);

        String document = """
            mutation {
                createProduct(input: {
                    title: "Laptop",
                    quantity: 3,
                    price: 1499.99,
                    userId: "1",
                    categoryId: "2",
                    description: "High-end"
                }) {
                    id
                    title
                    price
                    quantity
                }
            }
        """;

        graphQlTester.document(document)
                .execute()
                .path("createProduct.id").entity(String.class).isEqualTo("3")
                .path("createProduct.title").entity(String.class).isEqualTo("Laptop")
                .path("createProduct.price").entity(BigDecimal.class).isEqualTo(new BigDecimal("1499.99"))
                .path("createProduct.quantity").entity(Integer.class).isEqualTo(3);

        verify(productService).create(any(ProductRequest.class));
    }

    @Test
    void updateProduct_mutationReturnsUpdatedProduct() {
        ProductResponse updated = new ProductResponse(3L, "Laptop Pro", 4, "High-end Pro", new BigDecimal("1799.99"), 1L, 2L, "Electronics", "Alice");
        when(productService.update(eq(3L), any(ProductRequest.class))).thenReturn(updated);

        String document = """
            mutation {
                updateProduct(id: "3", input: {
                    title: "Laptop Pro",
                    quantity: 4,
                    price: 1799.99,
                    userId: "1",
                    categoryId: "2",
                    description: "High-end Pro"
                }) {
                    id
                    title
                    price
                }
            }
        """;

        graphQlTester.document(document)
                .execute()
                .path("updateProduct.id").entity(String.class).isEqualTo("3")
                .path("updateProduct.title").entity(String.class).isEqualTo("Laptop Pro")
                .path("updateProduct.price").entity(BigDecimal.class).isEqualTo(new BigDecimal("1799.99"));

        verify(productService).update(eq(3L), any(ProductRequest.class));
    }

    @Test
    void deleteProduct_mutationReturnsBoolean() {
        when(productService.delete(3L)).thenReturn(true);

        String document = """
            mutation {
                deleteProduct(id: "3")
            }
        """;

        graphQlTester.document(document)
                .execute()
                .path("deleteProduct").entity(Boolean.class).isEqualTo(true);

        verify(productService).delete(3L);
    }
}
