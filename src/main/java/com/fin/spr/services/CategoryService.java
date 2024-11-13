package com.fin.spr.services;

import com.fin.spr.interfaces.service.ICategoryService;
import com.fin.spr.models.Category;
import com.fin.spr.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * The {@code CategoryService} class provides the implementation of the {@link ICategoryService} interface
 * for managing categories. It utilizes an in-memory storage solution to perform CRUD operations
 * on {@link Category} entities.
 */
@Service
public class CategoryService implements ICategoryService {


    private final InMemoryStorage<Category, Integer> categoryStorage;

    /**
     * Service class responsible for managing categories.
     *
     * <p>This class uses an in-memory storage mechanism for storing
     * and retrieving {@link Category} entities. The storage is injected
     * via constructor dependency injection.</p>
     *
     * @param categoryStorage the in-memory storage for {@link Category} entities.
     */
    @Autowired
    public CategoryService(InMemoryStorage<Category, Integer> categoryStorage) {
        this.categoryStorage = categoryStorage;
    }

    /**
     * Retrieves a list of all categories stored in the system.
     *
     * @return a list of all {@link Category} entities
     */
    @Override
    public List<Category> getAllCategories() {
        return categoryStorage.getAll();
    }

    /**
     * Retrieves a category by its identifier.
     *
     * @param id the identifier of the category to retrieve
     * @return an {@link Optional} containing the found {@link Category}, or empty if not found
     */
    @Override
    public Optional<Category> getCategoryById(Integer id) {
        return categoryStorage.getById(id);
    }

    /**
     * Creates a new category in the storage.
     *
     * @param category the {@link Category} entity to be created
     */
    @Override
    public void createCategory(Category category) {
        categoryStorage.create(category.getId(), category);
    }

    /**
     * Updates an existing category in the storage.
     *
     * @param id       the identifier of the category to update
     * @param category the updated {@link Category} entity
     * @return true if the update was successful, false if the category was not found
     */
    @Override
    public boolean updateCategory(Integer id, Category category) {
        return categoryStorage.update(id, category);
    }

    /**
     * Deletes a category from the storage by its identifier.
     *
     * @param id the identifier of the category to be deleted
     * @return true if the deletion was successful, false if the category was not found
     */
    @Override
    public boolean deleteCategory(Integer id) {
        return categoryStorage.delete(id);
    }
}
