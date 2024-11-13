package com.fin.spr.controllers;

import com.fin.spr.annotations.LogExecutionTime;
import com.fin.spr.exceptions.EntityAlreadyExistsException;
import com.fin.spr.interfaces.controller.ICategoryController;
import com.fin.spr.interfaces.service.ICategoryService;
import com.fin.spr.models.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing categories in the application.
 * This controller provides endpoints to perform CRUD operations on categories.
 */
@RestController
@RequestMapping("/api/v1/places/categories")
@LogExecutionTime
public class CategoryController implements ICategoryController {

    private final ICategoryService categoryService;

    /**
     * REST controller responsible for managing categories in the application.
     *
     * <p>This controller provides endpoints for performing CRUD operations
     * on {@link Category} entities. The business logic is handled by
     * an {@link ICategoryService} implementation, which is injected via
     * constructor dependency injection.</p>
     *
     * @param categoryService the service responsible for category-related operations.
     */
    @Autowired
    public CategoryController(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Retrieves all categories.
     *
     * @return a list of all categories and an HTTP status code.
     */
    @GetMapping
    @Override
    @ResponseStatus(HttpStatus.OK)
    public List<Category> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return categories;
    }

    /**
     * Retrieves a category by its ID.
     *
     * @param id the ID of the category to retrieve.
     * @return a ResponseEntity containing the found category or an HTTP 404 status code if not found.
     */
    @GetMapping("/{id}")
    @Override
    public ResponseEntity<Category> getCategoryById(@PathVariable Integer id) {
        Optional<Category> category = categoryService.getCategoryById(id);
        return category.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Creates a new category.
     *
     * @param category the category to create.
     * @return a ResponseEntity containing the created category and an HTTP status code.
     *         Returns HTTP 409 if the category already exists.
     */
    @PostMapping
    @Override
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        try {
            categoryService.createCategory(category);
            return ResponseEntity.status(201).body(category);
        } catch (EntityAlreadyExistsException e) {
            return ResponseEntity.status(409).body(null);
        }
    }

    /**
     * Updates an existing category.
     *
     * @param id the ID of the category to update.
     * @param category the updated category data.
     * @return a ResponseEntity containing the updated category or an HTTP 404 status code if not found.
     */
    @PutMapping("/{id}")
    @Override
    public ResponseEntity<Category> updateCategory(@PathVariable Integer id, @RequestBody Category category) {
        boolean updated = categoryService.updateCategory(id, category);
        return updated ? ResponseEntity.ok(category) : ResponseEntity.notFound().build();
    }

    /**
     * Deletes a category by its ID.
     *
     * @param id the ID of the category to delete.
     * @return a ResponseEntity with an HTTP status code indicating the result of the operation.
     *         Returns HTTP 204 if the category was deleted, or HTTP 404 if not found.
     */
    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        boolean deleted = categoryService.deleteCategory(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
