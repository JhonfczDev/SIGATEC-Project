package com.sigatec.repository.memory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import com.sigatec.model.Identifiable;
import com.sigatec.repository.Repository;

/**
 * Reusable in-memory implementation of {@link Repository} backed by a map.
 * Keeps insertion order. Concrete repositories extend it and add their own lookups
 * with {@link #findWhere} (DRY, Open/Closed). Not thread-safe (console application).
 */
public abstract class InMemoryRepository<T extends Identifiable<K>, K> implements Repository<T, K> {

    private final Map<K, T> store = new LinkedHashMap<>();

    @Override
    public void save(T entity) {
        Objects.requireNonNull(entity, "entity must not be null");
        store.put(entity.getId(), entity);
    }

    @Override
    public Optional<T> findById(K id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<T> findAll() {
        return List.copyOf(store.values());
    }

    @Override
    public boolean existsById(K id) {
        return store.containsKey(id);
    }

    @Override
    public boolean deleteById(K id) {
        return store.remove(id) != null;
    }

    /** Entities matching the filter, in insertion order. */
    protected List<T> findWhere(Predicate<? super T> filter) {
        return store.values().stream().filter(filter).toList();
    }

    /** First entity matching the filter. */
    protected Optional<T> findFirstWhere(Predicate<? super T> filter) {
        return store.values().stream().filter(filter).findFirst();
    }
}

