package com.fin.spr.interfaces;

import com.fin.spr.models.Category;

import java.util.List;
import java.util.Optional;

/**
 * The {@code ICategoryService} interface defines the contract for services
 * managing {@link Category} entities. It provides methods to perform
 * operations such as retrieving, creating, updating, and deleting categories.
 */
public interface ICategoryService {

    /**
     * Retrieves a list of all categories.
     *
     * @return a {@link List} containing all {@link Category} entities
     */
    List<Category> getAllCategories();

    /**
     * Retrieves a category by its identifier.
     *
     * @param id the identifier of the category to retrieve
     * @return an {@link Optional} containing the found {@link Category}, or empty if not found
     */
    Optional<Category> getCategoryById(Integer id);

    /**
     * Creates a new category with the specified identifier.
     *
     * @param id       the identifier for the new category
     * @param category the {@link Category} entity to be created
     */
    void createCategory(Integer id, Category category);

    /**
     * Updates an existing category identified by its identifier.
     *
     * @param id       the identifier of the category to update
     * @param category the updated {@link Category} entity
     * @return true if the update was successful, false if the category was not found
     */
    boolean updateCategory(Integer id, Category category);

    /**
     * Deletes a category identified by its identifier.
     *
     * @param id the identifier of the category to be deleted
     * @return true if the deletion was successful, false if the category was not found
     */
    boolean deleteCategory(Integer id);
}
