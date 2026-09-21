package vn.edu.hcmute.bt09graphql.controller.graphql;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import vn.edu.hcmute.bt09graphql.dto.request.CategoryRequest;
import vn.edu.hcmute.bt09graphql.dto.response.CategoryResponse;
import vn.edu.hcmute.bt09graphql.service.CategoryService;

import java.util.List;

@Controller
public class CategoryGraphQlController {

    private final CategoryService categoryService;

    public CategoryGraphQlController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @QueryMapping
    public List<CategoryResponse> categories() {
        return categoryService.findAll();
    }

    @QueryMapping
    public CategoryResponse category(@Argument Long id) {
        return categoryService.findById(id);
    }

    @MutationMapping
    public CategoryResponse createCategory(@Argument CategoryRequest input) {
        return categoryService.create(input);
    }

    @MutationMapping
    public CategoryResponse updateCategory(@Argument Long id, @Argument CategoryRequest input) {
        return categoryService.update(id, input);
    }

    @MutationMapping
    public Boolean deleteCategory(@Argument Long id) {
        return categoryService.delete(id);
    }
}
