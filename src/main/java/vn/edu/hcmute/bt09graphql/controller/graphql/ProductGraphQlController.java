package vn.edu.hcmute.bt09graphql.controller.graphql;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import vn.edu.hcmute.bt09graphql.dto.request.ProductRequest;
import vn.edu.hcmute.bt09graphql.dto.response.ProductResponse;
import vn.edu.hcmute.bt09graphql.service.ProductService;

import java.util.List;

@Controller
public class ProductGraphQlController {

    private final ProductService productService;

    public ProductGraphQlController(ProductService productService) {
        this.productService = productService;
    }

    @QueryMapping
    public List<ProductResponse> products() {
        return productService.findAll();
    }

    @QueryMapping
    public ProductResponse product(@Argument Long id) {
        return productService.findById(id);
    }

    @QueryMapping
    public List<ProductResponse> productsByPriceAsc() {
        return productService.findAllOrderByPriceAsc();
    }

    @QueryMapping
    public List<ProductResponse> productsByCategory(@Argument Long categoryId) {
        return productService.findByCategoryId(categoryId);
    }

    @MutationMapping
    public ProductResponse createProduct(@Argument ProductRequest input) {
        return productService.create(input);
    }

    @MutationMapping
    public ProductResponse updateProduct(@Argument Long id, @Argument ProductRequest input) {
        return productService.update(id, input);
    }

    @MutationMapping
    public Boolean deleteProduct(@Argument Long id) {
        return productService.delete(id);
    }
}
