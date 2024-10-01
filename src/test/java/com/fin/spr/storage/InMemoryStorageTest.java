package com.fin.spr.storage;

import com.fin.spr.exceptions.EntityAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryStorageTest {

    private InMemoryStorage<String, Integer> storage;

    @BeforeEach
    void setUp() {
        storage = new InMemoryStorage<>();
    }

    @Test
    void testCreateAndGet() {
        storage.create(1, "Test Entity");

        assertThat(storage.getAll())
                .hasSize(1);

        assertThat(storage.getById(1))
                .isPresent()
                .hasValue("Test Entity");
    }

    @Test
    void testGetAll() {
        storage.create(1, "First Entity");
        storage.create(2, "Second Entity");

        assertThat(storage.getAll())
                .hasSize(2)
                .containsExactly("First Entity", "Second Entity");
    }

    @Test
    void testUpdate() {
        storage.create(1, "Initial Entity");
        storage.update(1, "Updated Entity");

        assertThat(storage.getById(1))
                .isPresent()
                .hasValue("Updated Entity");

        assertThat(storage.getAll()).hasSize(1);
    }

    @Test
    void testDelete() {
        storage.create(1, "Entity to be deleted");

        boolean deleted = storage.delete(1);

        assertThat(deleted).isTrue();
        assertThat(storage.getById(1)).isNotPresent();

        assertThat(storage.getAll()).isEmpty();
    }

    @Test
    void testCreateAlreadyExists() {
        storage.create(1, "Existing Entity");

        assertThatThrownBy(() -> storage.create(1, "Another Entity"))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessageContaining("Entity with ID 1 already exists.");

        assertThat(storage.getAll()).hasSize(1);
    }

    @Test
    void testUpdateNonExistentEntity() {
        boolean updated = storage.update(1, "Non-existent Entity");

        assertThat(updated).isFalse();
        assertThat(storage.getAll()).isEmpty();
    }

    @Test
    void testDeleteNonExistentEntity() {
        boolean deleted = storage.delete(1);

        assertThat(deleted).isFalse();
        assertThat(storage.getAll()).isEmpty();
    }
}