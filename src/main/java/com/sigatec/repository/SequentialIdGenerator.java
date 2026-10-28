package com.sigatec.repository;

/**
 * Generates sequential identifiers such as {@code GAR-0001}, {@code GAR-0002}...
 *
 * <p>It can be seeded with the last used number, and it can {@link #observe} existing ids
 * so that the sequence always continues after the highest one already in use.
 * Not thread-safe (console application).</p>
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

    /**
     * Takes note of an id already in use: if it has this generator's prefix and a higher
     * number than the last one, the sequence will continue after it. Other ids are ignored.
     */
    public void observe(String id) {
        if (id == null || !id.startsWith(prefix)) {
            return;
        }
        try {
            int number = Integer.parseInt(id.substring(prefix.length()));
            if (number > last) {
                last = number;
            }
        } catch (NumberFormatException e) {
            // Not a generated id (e.g. typed by hand): it cannot collide with the sequence.
        }
    }
}
