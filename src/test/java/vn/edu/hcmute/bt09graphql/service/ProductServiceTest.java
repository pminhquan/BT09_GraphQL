package vn.edu.hcmute.bt09graphql.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.hcmute.bt09graphql.dto.request.ProductRequest;
import vn.edu.hcmute.bt09graphql.entity.Category;
import vn.edu.hcmute.bt09graphql.entity.Product;
import vn.edu.hcmute.bt09graphql.entity.User;
import vn.edu.hcmute.bt09graphql.repository.CategoryRepository;
import vn.edu.hcmute.bt09graphql.repository.ProductRepository;
import vn.edu.hcmute.bt09graphql.repository.UserRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void create_missingUser_throwsIllegalArgumentException() {
        ProductRequest request = new ProductRequest("Phone", 10, "Smartphone", BigDecimal.valueOf(999), 99L, 1L);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> productService.create(request));
        verify(productRepository, never()).save(any());
    }

    @Test
    void create_missingCategory_throwsIllegalArgumentException() {
        ProductRequest request = new ProductRequest("Phone", 10, "Smartphone", BigDecimal.valueOf(999), 1L, 99L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> productService.create(request));
        verify(productRepository, never()).save(any());
    }

    @Test
    void update_missingCategory_throwsIllegalArgumentException() {
        Long productId = 5L;
        ProductRequest request = new ProductRequest("Phone", 10, "Smartphone", BigDecimal.valueOf(999), 1L, 99L);
        when(productRepository.findById(productId)).thenReturn(Optional.of(new Product()));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> productService.update(productId, request));
        verify(productRepository, never()).save(any());
    }
}
