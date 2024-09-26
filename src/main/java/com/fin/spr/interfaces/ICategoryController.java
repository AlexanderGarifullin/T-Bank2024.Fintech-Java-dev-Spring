package com.fin.spr.interfaces;

import com.fin.spr.models.Category;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * The {@code ICategoryController} interface defines the contract for managing category-related operations
 * in the application. It provides methods for performing CRUD operations on {@link Category} entities.
 */
public interface ICategoryController {

    /**
     * Retrieves a list of all categories.
     *
     * @return a list of {@link Category} entities
     */
    List<Category> getAllCategories();

    /**
     * Retrieves a category by its identifier.
     *
     * @param id the identifier of the category to retrieve
     * @return a {@link ResponseEntity} containing the found {@link Category}, or an error response if not found
     */
    ResponseEntity<Category> getCategoryById(Integer  id);

    /**
     * Creates a new category.
     *
     * @param category the {@link Category} entity to be created
     * @return a {@link ResponseEntity} containing the created {@link Category}
     */
    ResponseEntity<Category> createCategory(Category category);

    /**
     * Updates an existing category by its identifier.
     *
     * @param id       the identifier of the category to update
     * @param category the updated {@link Category} entity
     * @return a {@link ResponseEntity} containing the updated {@link Category}, or an error response if not found
     */
    ResponseEntity<Category> updateCategory(Integer  id, Category category);

    /**
     * Deletes a category by its identifier.
     *
     * @param id the identifier of the category to be deleted
     * @return a {@link ResponseEntity} indicating the outcome of the deletion operation
     */
    ResponseEntity<Void> deleteCategory(Integer  id);
}
