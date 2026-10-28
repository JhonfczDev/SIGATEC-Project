package com.sigatec.repository;

import java.util.List;
import java.util.Optional;

import com.sigatec.model.Identifiable;

/**
 * Generic contract for storing and retrieving domain entities.
 *
 * <p>Upper layers (Service) depend on this abstraction and not on a concrete storage
 * (Dependency Inversion): the in-memory implementation can later be replaced by a
 * file or database one without touching the business logic.</p>
 *
 * @param <T> entity type
 * @param <K> identifier type
 */
public interface Repository<T extends Identifiable<K>, K> {

    /** Inserts the entity, or replaces the existing one with the same id. */
    void save(T entity);

    Optional<T> findById(K id);

    /** All stored entities, in insertion order (read-only snapshot). */
    List<T> findAll();

    boolean existsById(K id);

    /** @return {@code true} if an entity was removed, {@code false} if the id did not exist */
    boolean deleteById(K id);
}