package com.sigatec.repository.file;

import java.util.ArrayList;
import java.util.List;

/**
 * Encodes/decodes one record as a single text line with fields separated by {@code |}.
 * Backslash, pipe and line breaks inside a field are escaped, so any text
 * (e.g. a claim description) can be stored safely.
 */
final class RecordCodec {

    private static final char SEPARATOR = '|';
    private static final char ESCAPE = '\\';

    private RecordCodec() {
    }

    static String encode(String... fields) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                line.append(SEPARATOR);
            }
            for (char c : fields[i].toCharArray()) {
                switch (c) {
                    case ESCAPE -> line.append(ESCAPE).append(ESCAPE);
                    case SEPARATOR -> line.append(ESCAPE).append(SEPARATOR);
                    case '\n' -> line.append(ESCAPE).append('n');
                    case '\r' -> line.append(ESCAPE).append('r');
                    default -> line.append(c);
                }
            }
        }
        return line.toString();
    }

    static String[] decode(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == ESCAPE) {
                if (i + 1 >= line.length()) {
                    throw new IllegalArgumentException("secuencia de escape incompleta al final de la línea");
                }
                char next = line.charAt(++i);
                current.append(switch (next) {
                    case 'n' -> '\n';
                    case 'r' -> '\r';
                    default -> next;
                });
            } else if (c == SEPARATOR) {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }
}
