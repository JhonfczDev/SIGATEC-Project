package com.sigatec.repository;

/**
 * Generates sequential identifiers such as {@code GAR-0001}, {@code GAR-0002}...
 *
 * <p>It can be seeded with the last used number so that a future persistent repository
 * can continue the sequence after restarting. Not thread-safe (console application).</p>
 */
public final class SequentialIdGenerator {

    private final String prefix;
    private int last;

    public SequentialIdGenerator(String prefix) {
        this(prefix, 0);
    }

    public SequentialIdGenerator(String prefix, int lastUsed) {
        if (prefix == null || prefix.isBlank()) {
            throw new IllegalArgumentException("prefix must not be blank");
        }
        if (lastUsed < 0) {
            throw new IllegalArgumentException("lastUsed must not be negative");
        }
        this.prefix = prefix;
        this.last = lastUsed;
    }

    public String next() {
        last++;
        return String.format("%s%04d", prefix, last);
    }
}
