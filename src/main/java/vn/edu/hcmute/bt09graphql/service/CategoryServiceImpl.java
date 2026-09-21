package vn.edu.hcmute.bt09graphql.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.hcmute.bt09graphql.dto.request.CategoryRequest;
import vn.edu.hcmute.bt09graphql.dto.response.CategoryResponse;
import vn.edu.hcmute.bt09graphql.entity.Category;
import vn.edu.hcmute.bt09graphql.entity.Product;
import vn.edu.hcmute.bt09graphql.entity.User;
import vn.edu.hcmute.bt09graphql.repository.CategoryRepository;
import vn.edu.hcmute.bt09graphql.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse findById(Long id) {
        if (id == null) {
            return null;
        }
        return categoryRepository.findById(id)
                .map(this::toResponse)
                .orElse(null);
    }

    @Override
    public CategoryResponse create(CategoryRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Category request must not be null");
        }
        Category category = new Category();
        category.setName(request.getName());
        category.setImages(request.getImages());
        Category saved = categoryRepository.save(category);
        return toResponse(saved);
    }

    @Override
    public CategoryResponse update(Long id, CategoryRequest request) {
        if (id == null) {
            throw new IllegalArgumentException("Category id must not be null");
        }
        if (request == null) {
            throw new IllegalArgumentException("Category request must not be null");
        }
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));
        category.setName(request.getName());
        category.setImages(request.getImages());
        Category updated = categoryRepository.save(category);
        return toResponse(updated);
    }

    @Override
    public boolean delete(Long id) {
        if (id == null) {
            return false;
        }
        return categoryRepository.findById(id).map(category -> {
            List<Product> products = productRepository.findByCategoryId(id);
            if (!products.isEmpty()) {
                throw new IllegalStateException("Cannot delete category with id " + id + " because it still has associated products");
            }
            for (User user : category.getUsers()) {
                user.getCategories().remove(category);
            }
            categoryRepository.delete(category);
            return true;
        }).orElse(false);
    }

    private CategoryResponse toResponse(Category category) {
        if (category == null) {
            return null;
        }
        return new CategoryResponse(category.getId(), category.getName(), category.getImages());
    }
}
