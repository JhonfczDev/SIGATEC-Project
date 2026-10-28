package com.sigatec.repository.file;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.sigatec.repository.PersistenceException;

/**
 * Reads and writes one UTF-8 text file where each line is a record (see {@link RecordCodec}).
 * Lines that are blank or start with {@code #} are ignored when reading.
 *
 * <p>Writing is safe: the content goes to a temporary file that then replaces the original,
 * so a crash in the middle never leaves a half-written data file.</p>
 *
 * <p>This is the only class that knows about files; the repositories only describe how to
 * convert an entity to/from its fields (Single Responsibility).</p>
 */
final class TextFileStore {

    private static final char BOM = '\uFEFF';

    private final Path file;
    private final String header;

    TextFileStore(Path file, String header) {
        this.file = file;
        this.header = header;
    }

    /** Parses every record line and hands the result to {@code sink}. Does nothing if the file does not exist. */
    <T> void load(Function<String[], T> parser, Consumer<T> sink) {
        if (!Files.exists(file)) {
            return;
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PersistenceException("No se pudo leer " + file + ": " + e.getMessage(), e);
        }
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (i == 0 && !line.isEmpty() && line.charAt(0) == BOM) {
                line = line.substring(1);
            }
            if (line.isBlank() || line.startsWith("#")) {
                continue;
            }
            try {
                sink.accept(parser.apply(RecordCodec.decode(line)));
            } catch (RuntimeException e) {
                throw new PersistenceException("Error al leer " + file.getFileName()
                        + " (línea " + (i + 1) + "): " + e.getMessage(), e);
            }
        }
    }

    /** Rewrites the whole file with the given entities. */
    <T> void save(List<T> entities, Function<T, String[]> toFields) {
        List<String> lines = new ArrayList<>();
        lines.add("# " + header);
        for (T entity : entities) {
            lines.add(RecordCodec.encode(toFields.apply(entity)));
        }
        try {
            Path folder = file.toAbsolutePath().getParent();
            Files.createDirectories(folder);
            Path temp = folder.resolve(file.getFileName() + ".tmp");
            Files.write(temp, lines, StandardCharsets.UTF_8);
            try {
                Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new PersistenceException("No se pudo guardar " + file + ": " + e.getMessage(), e);
        }
    }

    /** Checks that a parsed line has exactly the expected number of fields. */
    static void requireFieldCount(String[] fields, int expected) {
        if (fields.length != expected) {
            throw new PersistenceException("se esperaban " + expected + " campos y hay " + fields.length);
        }
    }
}
