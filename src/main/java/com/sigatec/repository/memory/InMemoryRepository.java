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
 *
 * <p>Template-method hook: {@link #afterChange()} runs after every successful
 * {@code save}/{@code deleteById}. Persistent repositories override it to write to their
 * storage; if it throws, the in-memory change is rolled back so memory and storage never diverge.</p>
 */
public abstract class InMemoryRepository<T extends Identifiable<K>, K> implements Repository<T, K> {

    private final Map<K, T> store = new LinkedHashMap<>();

    @Override
    public void save(T entity) {
        Objects.requireNonNull(entity, "entity must not be null");
        Map<K, T> backup = new LinkedHashMap<>(store);
        register(entity);
        notifyChange(backup);
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
        if (!store.containsKey(id)) {
            return false;
        }
        Map<K, T> backup = new LinkedHashMap<>(store);
        store.remove(id);
        notifyChange(backup);
        return true;
    }

    /** Puts the entity in memory without triggering {@link #afterChange()} (used when loading). */
    protected void register(T entity) {
        store.put(entity.getId(), entity);
    }

    /** Like {@link #register} but rejects an id that is already present. */
    protected final void registerUnique(T entity) {
        if (store.containsKey(entity.getId())) {
            throw new IllegalStateException("id duplicado: " + entity.getId());
        }
        register(entity);
    }

    /** Hook called after each successful change. Does nothing by default. */
    protected void afterChange() {
        // In-memory: nothing to do.
    }

    /** Entities matching the filter, in insertion order. */
    protected List<T> findWhere(Predicate<? super T> filter) {
        return store.values().stream().filter(filter).toList();
    }

    /** First entity matching the filter. */
    protected Optional<T> findFirstWhere(Predicate<? super T> filter) {
        return store.values().stream().filter(filter).findFirst();
    }

    private void notifyChange(Map<K, T> backup) {
        try {
            afterChange();
        } catch (RuntimeException e) {
            store.clear();
            store.putAll(backup);
            throw e;
        }
    }
}

