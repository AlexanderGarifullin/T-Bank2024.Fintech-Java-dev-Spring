package com.fin.spr.services;

import com.fin.spr.models.Category;
import com.fin.spr.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private InMemoryStorage<Category, Integer> categoryStorage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCategories() {
        Category category1 = new Category(1, "Slug 1", "Name 1");
        Category category2 = new Category(2, "Slug 2", "Name 2");

        when(categoryStorage.getAll()).thenReturn(List.of(category1, category2));

        List<Category> categories = categoryService.getAllCategories();
        assertThat(categories).hasSize(2)
                .containsExactly(category1, category2);
    }

    @Test
    void testGetCategoryById() {
        Category category = new Category(1, "Slug 1", "Name 1");

        when(categoryStorage.getById(1)).thenReturn(Optional.of(category));


        Optional<Category> foundCategory = categoryService.getCategoryById(1);
        assertThat(foundCategory).isPresent()
                .hasValue(category);
    }

    @Test
    void testCreateCategory() {
        Category category = new Category(1, "Slug 1", "Name 1");

        categoryService.createCategory(1, category);

        verify(categoryStorage, times(1)).create(1, category);
    }

    @Test
    void testUpdateCategory() {
        Category category = new Category(1, "Slug 1", "Name 1");
        Category updatedCategory = new Category(2, "Slug 2", "Name 2");

        when(categoryStorage.update(1, updatedCategory)).thenReturn(true);

        boolean result = categoryService.updateCategory(1, updatedCategory);

        assertThat(result).isTrue();
        verify(categoryStorage, times(1)).update(1, updatedCategory);
    }

    @Test
    void testDeleteCategory() {
        when(categoryStorage.delete(1)).thenReturn(true);

        boolean result = categoryService.deleteCategory(1);

        assertThat(result).isTrue();
        verify(categoryStorage, times(1)).delete(1);
    }

    @Test
    void testDeleteCategoryNotFound() {
        when(categoryStorage.delete(2)).thenReturn(false);

        boolean result = categoryService.deleteCategory(2);

        assertThat(result).isFalse();
        verify(categoryStorage, times(1)).delete(2);
    }
}
