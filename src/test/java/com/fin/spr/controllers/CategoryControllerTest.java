package com.fin.spr.controllers;

import com.fin.spr.exceptions.EntityAlreadyExistsException;
import com.fin.spr.interfaces.service.ICategoryService;
import com.fin.spr.models.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CategoryControllerTest {

    @InjectMocks
    private CategoryController categoryController;

    @Mock
    private ICategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCategories() {
        // Данные для теста
        Category category1 = new Category(1, "slug 1", "name ");
        Category category2 = new Category(2, "slug 2", "name ");

        when(categoryService.getAllCategories()).thenReturn(List.of(category1, category2));

        List<Category> response = categoryController.getAllCategories();
        assertThat(response).hasSize(2)
                .containsExactly(category1, category2);
    }

    @Test
    void testGetCategoryByIdFound() {
        Category category = new Category(1, "slug 1", "name ");

        when(categoryService.getCategoryById(1)).thenReturn(Optional.of(category));

        ResponseEntity<Category> response = categoryController.getCategoryById(1);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(category);
    }

    @Test
    void testGetCategoryByIdNotFound() {
        when(categoryService.getCategoryById(1)).thenReturn(Optional.empty());

        ResponseEntity<Category> response = categoryController.getCategoryById(1);
        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    void testCreateCategorySuccess() {
        Category category = new Category(1, "slug 1", "name ");

        doNothing().when(categoryService).createCategory(category);

        ResponseEntity<Category> response = categoryController.createCategory(category);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody()).isEqualTo(category);
    }


    @Test
    void testCreateCategoryAlreadyExists() {
        Category category = new Category(1, "slug 1", "name ");

        doThrow(EntityAlreadyExistsException.class).when(categoryService).createCategory(category);

        ResponseEntity<Category> response = categoryController.createCategory(category);

        assertThat(response.getStatusCodeValue()).isEqualTo(409);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void testUpdateCategorySuccess() {
        Category updatedCategory = new Category(1, "slug 1", "name ");

        when(categoryService.updateCategory(1, updatedCategory)).thenReturn(true);

        ResponseEntity<Category> response = categoryController.updateCategory(1, updatedCategory);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(updatedCategory);
    }

    @Test
    void testUpdateCategoryNotFound() {
        Category updatedCategory = new Category(1, "slug 1", "name ");

        when(categoryService.updateCategory(1, updatedCategory)).thenReturn(false);

        ResponseEntity<Category> response = categoryController.updateCategory(1, updatedCategory);
        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    void testDeleteCategorySuccess() {
        when(categoryService.deleteCategory(1)).thenReturn(true);

        ResponseEntity<Void> response = categoryController.deleteCategory(1);
        assertThat(response.getStatusCodeValue()).isEqualTo(204);
    }

    @Test
    void testDeleteCategoryNotFound() {
        when(categoryService.deleteCategory(1)).thenReturn(false);

        ResponseEntity<Void> response = categoryController.deleteCategory(1);
        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }
}
// ls7