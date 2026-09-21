package vn.edu.hcmute.bt09graphql.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.hcmute.bt09graphql.entity.Category;
import vn.edu.hcmute.bt09graphql.entity.Product;
import vn.edu.hcmute.bt09graphql.entity.User;
import vn.edu.hcmute.bt09graphql.repository.CategoryRepository;
import vn.edu.hcmute.bt09graphql.repository.ProductRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void delete_withExistingProducts_throwsIllegalStateExceptionAndDoesNotDelete() {
        Long categoryId = 1L;
        Category category = new Category("Electronics", "electronics.jpg");
        category.setId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(productRepository.findByCategoryId(categoryId)).thenReturn(List.of(new Product()));

        assertThrows(IllegalStateException.class, () -> categoryService.delete(categoryId));

        verify(productRepository, never()).delete(any());
        verify(productRepository, never()).deleteAll(any());
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void delete_withoutProducts_removesCategoryFromUsersAndDeletesCategory() {
        Long categoryId = 1L;
        Category category = new Category("Electronics", "electronics.jpg");
        category.setId(categoryId);

        User user1 = new User("Alice", "alice@example.com", "pass1", "111");
        user1.setId(10L);
        user1.getCategories().add(category);

        User user2 = new User("Bob", "bob@example.com", "pass2", "222");
        user2.setId(20L);
        user2.getCategories().add(category);

        category.getUsers().add(user1);
        category.getUsers().add(user2);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(productRepository.findByCategoryId(categoryId)).thenReturn(Collections.emptyList());

        boolean result = categoryService.delete(categoryId);

        assertTrue(result);
        assertFalse(user1.getCategories().contains(category));
        assertFalse(user2.getCategories().contains(category));

        verify(productRepository).findByCategoryId(categoryId);
        verify(productRepository, never()).delete(any());
        verify(productRepository, never()).deleteAll(any());
        verify(categoryRepository).delete(category);
    }
}
