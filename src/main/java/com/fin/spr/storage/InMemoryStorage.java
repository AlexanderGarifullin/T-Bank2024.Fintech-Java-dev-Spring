package com.fin.spr.storage;

import com.fin.spr.exceptions.EntityAlreadyExistsException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@code InMemoryStorage} class provides a thread-safe in-memory storage solution
 * for managing entities of type {@code T} with a specified identifier type {@code ID}.
 * It uses a {@link ConcurrentHashMap} for concurrent access and storage operations.
 *
 * @param <T> the type of entities to be stored
 * @param <ID> the type of the identifier for the entities
 */
public class InMemoryStorage<T, ID> {
    private final ConcurrentHashMap<ID, T> storage = new ConcurrentHashMap<>();

    /**
     * Retrieves all entities stored in memory.
     *
     * @return a list containing all stored entities
     */
    public List<T> getAll() {
        return List.copyOf(storage.values());
    }

    /**
     * Retrieves an entity by its identifier.
     *
     * @param id the identifier of the entity to retrieve
     * @return an {@link Optional} containing the found entity, or empty if not found
     */
    public Optional<T> getById(ID id) {
        return Optional.ofNullable(storage.get(id));
    }

    /**
     * Creates a new entity in the storage.
     *
     * @param id     the identifier for the entity
     * @param entity the entity to be stored
     * @throws EntityAlreadyExistsException if an entity with the given identifier already exists
     */
    public void create(ID id, T entity) {
        if (storage.containsKey(id)) {
            throw new EntityAlreadyExistsException(entityAlreadyExistsMsg(id));
        }
        storage.put(id, entity);
    }

    /**
     * Updates an existing entity in the storage.
     *
     * @param id     the identifier of the entity to update
     * @param entity the updated entity
     * @return true if the update was successful, false if the entity was not found
     */
    public boolean update(ID id, T entity) {
        if (storage.containsKey(id)) {
            storage.put(id, entity);
            return true;
        }
        return false;
    }

    /**
     * Deletes an entity from the storage by its identifier.
     *
     * @param id the identifier of the entity to be deleted
     * @return true if the deletion was successful, false if the entity was not found
     */
    public boolean delete(ID id) {
        return storage.remove(id) != null;
    }

    /**
     * Generates a message indicating that an entity with the specified identifier already exists.
     *
     * @param id the identifier of the entity
     * @return a string message indicating the existence of the entity
     */
    private String entityAlreadyExistsMsg(ID id) {
        return "Entity with ID " + id + " already exists.";
    }
}
