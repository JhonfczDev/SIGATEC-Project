package com.sigatec.model;

/**
 * Contract for every domain entity that has a unique identity.
 *
 * <p>Lets later layers (e.g. a generic {@code Repository<T extends Identifiable<K>, K>})
 * work with any entity without knowing its concrete type.</p>
 *
 * @param <K> identifier type
 */
public interface Identifiable<K> {

    K getId();
}