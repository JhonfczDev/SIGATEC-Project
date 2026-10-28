package com.sigatec.repository.file;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.sigatec.model.Claim;
import com.sigatec.model.ClaimStatus;
import com.sigatec.model.StatusChange;
import com.sigatec.model.Warranty;
import com.sigatec.repository.PersistenceException;
import com.sigatec.repository.WarrantyRepository;
import com.sigatec.repository.memory.InMemoryClaimRepository;

/**
 * Claim repository persisted in a text file.
 * Format: {@code id|warrantyId|description|claimDate|initialStatus|changes}, where
 * {@code changes} lists later status changes as {@code STATUS@yyyy-MM-dd} separated by {@code ;}
 * (empty if there are none). The full history is rebuilt on load by replaying the changes
 * through {@link Claim#changeStatus}, so the allowed transitions are checked again.
 *
 * <p>The warranty is resolved against the given repository, which must be loaded first.</p>
 */
public final class FileClaimRepository extends InMemoryClaimRepository {

    private final TextFileStore store;
    private final WarrantyRepository warranties;

    public FileClaimRepository(Path file, WarrantyRepository warranties) {
        this.warranties = warranties;
        this.store = new TextFileStore(file,
                "claims: id|warrantyId|description|claimDate|initialStatus|changes(STATUS@date;...)");
        store.load(this::parse, this::registerUnique);
    }

    @Override
    protected void afterChange() {
        store.save(findAll(), FileClaimRepository::toFields);
    }

    private Claim parse(String[] f) {
        TextFileStore.requireFieldCount(f, 6);
        Warranty warranty = warranties.findById(f[1])
                .orElseThrow(() -> new PersistenceException("garantía inexistente: " + f[1]));
        Claim claim = new Claim(f[0], warranty, f[2], LocalDate.parse(f[3]), ClaimStatus.valueOf(f[4]));
        if (!f[5].isBlank()) {
            for (String change : f[5].split(";")) {
                String[] parts = change.split("@");
                if (parts.length != 2) {
                    throw new PersistenceException("cambio de estado mal formado: '" + change + "'");
                }
                claim.changeStatus(ClaimStatus.valueOf(parts[0]), LocalDate.parse(parts[1]));
            }
        }
        return claim;
    }

    private static String[] toFields(Claim c) {
        List<StatusChange> history = c.getHistory();
        String changes = history.stream().skip(1)
                .map(h -> h.status().name() + "@" + h.date())
                .collect(Collectors.joining(";"));
        return new String[] {c.getId(), c.getWarranty().getId(), c.getDescription(),
                c.getClaimDate().toString(), history.get(0).status().name(), changes};
    }
}
