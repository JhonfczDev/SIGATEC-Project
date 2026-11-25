package com.sigatec.repository.jdbc;

import com.sigatec.model.Claim;
import com.sigatec.model.ClaimStatus;
import com.sigatec.model.StatusChange;
import com.sigatec.model.Warranty;
import com.sigatec.repository.ClaimRepository;
import com.sigatec.repository.PersistenceException;
import com.sigatec.repository.WarrantyRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class JdbcClaimRepository implements ClaimRepository {

    // "?" marks a value that is filled in later, so user input never becomes part of the SQL text.
    private static final String UPSERT_CLAIM = """
            INSERT INTO claims (id, warranty_id, description, claim_date, current_status)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                warranty_id = EXCLUDED.warranty_id,
                description = EXCLUDED.description,
                claim_date = EXCLUDED.claim_date,
                current_status = EXCLUDED.current_status
            """;

    private static final String DELETE_HISTORY =
            "DELETE FROM claim_status_history WHERE claim_id = ?";

    private static final String INSERT_HISTORY =
            "INSERT INTO claim_status_history (claim_id, status, change_date) VALUES (?, ?, ?)";

    private static final String SELECT_HISTORY =
            "SELECT status, change_date FROM claim_status_history WHERE claim_id = ? ORDER BY id";

    private static final String SELECT_COLUMNS =
            "SELECT id, warranty_id, description, claim_date FROM claims";

    private static final String SELECT_BY_ID = SELECT_COLUMNS + " WHERE id = ?";
    private static final String SELECT_ALL = SELECT_COLUMNS + " ORDER BY id";
    private static final String SELECT_BY_WARRANTY = SELECT_COLUMNS + " WHERE warranty_id = ? ORDER BY id";
    private static final String SELECT_BY_STATUS = SELECT_COLUMNS + " WHERE current_status = ? ORDER BY id";

    private static final String EXISTS =
            "SELECT 1 FROM claims WHERE id = ?";

    // The history rows are removed automatically (ON DELETE CASCADE in the table).
    private static final String DELETE =
            "DELETE FROM claims WHERE id = ?";

    // Takes the highest number already used (REC-0006 -> 6), so the next one can be 7.
    private static final String MAX_NUMBER =
            "SELECT COALESCE(MAX(CAST(SUBSTRING(id FROM 5) AS INT)), 0) FROM claims";

    private final WarrantyRepository warranties;

    public JdbcClaimRepository(WarrantyRepository warranties) {
        this.warranties = warranties;
    }

    @Override
    public String nextId() {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(MAX_NUMBER);
             ResultSet rs = statement.executeQuery()){

            rs.next();
            return String.format("REC-%04d", rs.getInt(1) + 1);

        } catch (SQLException e) {
            throw new PersistenceException("No se pudo generart el numero del reclamo.", e);
        }
    }

    @Override
    public List<Claim> findByWarrantyId(String warrantyId) {
        return query(SELECT_BY_WARRANTY, warrantyId, "No se pudo buscar los reclamos de la garantia.");
    }

    @Override
    public List<Claim> findByStatus(ClaimStatus status) {
        return query(SELECT_BY_STATUS, status.name(), "No se pudo buscar los reclamos por estado");
    }

    @Override
    public void save(Claim claim) {
        try(Connection connection = Database.getConnection()) {
            // Both tables are written togrther: if one fails, neither is changed.
            connection.setAutoCommit(false);

            try (PreparedStatement claimStatement = connection.prepareStatement(UPSERT_CLAIM);
                 PreparedStatement deleteStatement = connection.prepareStatement(DELETE_HISTORY);
                 PreparedStatement historyStatement = connection.prepareStatement(INSERT_HISTORY)) {

                claimStatement.setString(1, claim.getId());
                claimStatement.setString(2, claim.getWarranty().getId());
                claimStatement.setString(3, claim.getDescription());
                claimStatement.setDate(4, Date.valueOf(claim.getClaimDate()));
                claimStatement.setString(5, claim.getStatus().name());
                claimStatement.executeUpdate();

                // The history is written in order, so it always matches the Claim object.
                deleteStatement.setString(1, claim.getId());
                deleteStatement.executeUpdate();

                for (StatusChange change : claim.getHistory()){
                    historyStatement.setString(1, claim.getId());
                    historyStatement.setString(2, change.status().name());
                    historyStatement.setDate(3, Date.valueOf(change.date()));
                    historyStatement.addBatch();
                }
                historyStatement.executeBatch();

                connection.commit();

            }catch (SQLException e){
                connection.rollback();
                throw e;
            }
        }catch (SQLException e){
            throw new PersistenceException("No se pudo guardar el reclamo.", e);
        }
    }

    @Override
    public Optional<Claim> findById(String id) {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID)) {
            statement.setString(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenceException("No se pudo buscar el reclamo.", e);
        }
    }

    @Override
    public List<Claim> findAll() {
        return query(SELECT_ALL, null, "No se pudieron cargar los reclamos.");
    }

    @Override
    public boolean existsById(String id) {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(EXISTS)) {
            statement.setString(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new PersistenceException("No se pudo verificar el reclamo.", e);
        }
    }

    @Override
    public boolean deleteById(String id) {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE)) {
            statement.setString(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenceException("No se pudo eliminar el reclamo.", e);
        }
    }

    // Runs a query that may take one parameter or none, and converts every row.
    private List<Claim> query(String sql, String parameter, String errorMessage) {
        List<Claim> result = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (parameter != null) {
                statement.setString(1, parameter);
            }
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new PersistenceException(errorMessage, e);
        }
        return result;
    }

    // Rebuilds a Claim: the first history row is the initial status, and every
    // later row is replayed with changeStatus, so the Claim checks its own rules again.
    private Claim mapRow(ResultSet rs) throws SQLException {
        String claimId = rs.getString("id");
        String warrantyId = rs.getString("warranty_id");

        Warranty warranty = warranties.findById(warrantyId)
                .orElseThrow(() -> new PersistenceException("Falta la garantía " + warrantyId));

        List<StatusChange> history = loadHistory(claimId);
        if (history.isEmpty()) {
            throw new PersistenceException("El reclamo " + claimId + " no tiene historial.");
        }

        StatusChange first = history.get(0);
        Claim claim = new Claim(
                claimId,
                warranty,
                rs.getString("description"),
                rs.getDate("claim_date").toLocalDate(),
                first.status());

        for (StatusChange change : history.subList(1, history.size())) {
            claim.changeStatus(change.status(), change.date());
        }
        return claim;
    }

    private List<StatusChange> loadHistory(String claimId) throws SQLException {
        List<StatusChange> history = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_HISTORY)) {
            statement.setString(1, claimId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    history.add(new StatusChange(
                            ClaimStatus.valueOf(rs.getString("status")),
                            rs.getDate("change_date").toLocalDate()));
                }
            }
        }
        return history;
    }
}
