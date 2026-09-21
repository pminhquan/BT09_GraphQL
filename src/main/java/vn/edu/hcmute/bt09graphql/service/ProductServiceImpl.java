package vn.edu.hcmute.bt09graphql.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.hcmute.bt09graphql.dto.request.ProductRequest;
import vn.edu.hcmute.bt09graphql.dto.response.ProductResponse;
import vn.edu.hcmute.bt09graphql.entity.Category;
import vn.edu.hcmute.bt09graphql.entity.Product;
import vn.edu.hcmute.bt09graphql.entity.User;
import vn.edu.hcmute.bt09graphql.repository.CategoryRepository;
import vn.edu.hcmute.bt09graphql.repository.ProductRepository;
import vn.edu.hcmute.bt09graphql.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              UserRepository userRepository,
                              CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> findAllOrderByPriceAsc() {
        return productRepository.findAllByOrderByPriceAsc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> findByCategoryId(Long categoryId) {
        if (categoryId == null) {
            return List.of();
        }
        return productRepository.findByCategoryId(categoryId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        if (id == null) {
            return null;
        }
        return productRepository.findById(id)
                .map(this::toResponse)
                .orElse(null);
    }

    @Override
    public ProductResponse create(ProductRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Product request must not be null");
        }
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        if (request.getCategoryId() == null) {
            throw new IllegalArgumentException("Category ID must not be null");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + request.getUserId()));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + request.getCategoryId()));

        Product product = new Product();
        product.setTitle(request.getTitle());
        product.setQuantity(request.getQuantity());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setUser(user);
        product.setCategory(category);

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    @Override
    public ProductResponse update(Long id, ProductRequest request) {
        if (id == null) {
            throw new IllegalArgumentException("Product id must not be null");
        }
        if (request == null) {
            throw new IllegalArgumentException("Product request must not be null");
        }
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        if (request.getCategoryId() == null) {
            throw new IllegalArgumentException("Category ID must not be null");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + request.getUserId()));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + request.getCategoryId()));

        product.setTitle(request.getTitle());
        product.setQuantity(request.getQuantity());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setUser(user);
        product.setCategory(category);

        Product updated = productRepository.save(product);
        return toResponse(updated);
    }

    @Override
    public boolean delete(Long id) {
        if (id == null) {
            return false;
        }
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }
        Long userId = product.getUser() != null ? product.getUser().getId() : null;
        Long categoryId = product.getCategory() != null ? product.getCategory().getId() : null;
        String categoryName = product.getCategory() != null ? product.getCategory().getName() : null;
        String userFullname = product.getUser() != null ? product.getUser().getFullname() : null;

        return new ProductResponse(
                product.getId(),
                product.getTitle(),
                product.getQuantity(),
                product.getDescription(),
                product.getPrice(),
                userId,
                categoryId,
                categoryName,
                userFullname
        );
    }
}
