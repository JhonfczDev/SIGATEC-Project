package com.sigatec.repository.jdbc;

import com.sigatec.model.Customer;
import com.sigatec.repository.CustomerRepository;
import com.sigatec.repository.PersistenceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class JdbcCustomerRepository implements CustomerRepository {

    // "?" marks a value that is filled in later, so user input never becomes part of the SQL text.
    private static final String UPSERT = """
            INSERT INTO customers (document_number, full_name, phone, email)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (document_number) DO UPDATE SET
                full_name = EXCLUDED.full_name,
                phone = EXCLUDED.phone,
                email = EXCLUDED.email
            """;

    private static final String SELECT_BY_ID =
            "SELECT document_number, full_name, phone, email FROM customers WHERE document_number = ?";

    private static final String SELECT_ALL =
            "SELECT document_number, full_name, phone, email FROM customers ORDER BY document_number";

    private static final String EXISTS =
            "SELECT 1 FROM customers WHERE document_number = ?";

    private static final String DELETE =
            "DELETE FROM customers WHERE document_number = ?";

    @Override
    public Optional<Customer> findByDocumentNumber(String documentNumber) {
        return findById(documentNumber);
    }

    @Override
    public void save(Customer customer) {
        try(Connection connection = Database.getConnection();
            PreparedStatement statement = connection.prepareStatement(UPSERT)) {

            statement.setString(1, customer.getDocumentNumber());
            statement.setString(2, customer.getFullName());
            statement.setString(3, customer.getPhone());
            statement.setString(4, customer.getEmail());
            statement.executeUpdate();

        }catch (SQLException e){
            throw new PersistenceException("No se pudo guardar el cliente.", e);
        }
    }

    @Override
    public Optional<Customer> findById(String id) {
        try(Connection connection = Database.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID)){
            statement.setString(1, normalize(id));

            try(ResultSet rs = statement.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }catch (SQLException e){
            throw new PersistenceException("No se pudo buscar el cliente.", e);
        }
    }

    @Override
    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();

        try(Connection connection = Database.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_ALL);
            ResultSet rs = statement.executeQuery()) {

            while (rs.next()){
                customers.add(mapRow(rs));
            }
        }catch (SQLException e){
            throw new PersistenceException("No se pudieron cargar los clientes.", e);
        }
        return customers;
    }

    @Override
    public boolean existsById(String id) {
        try(Connection connection = Database.getConnection();
            PreparedStatement statement = connection.prepareStatement(EXISTS)) {
            statement.setString(1, normalize(id));

            try(ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }catch (SQLException e){
            throw new PersistenceException("No se pudo verificar el cliente.", e);
        }
    }

    @Override
    public boolean deleteById(String id) {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE)) {
            statement.setString(1, normalize(id));
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            // Fails if a warranty still uses this customer (foreign key rule).
            throw new PersistenceException("No se pudo eliminar el cliente.", e);
        }
    }

    // Converts the current row into a Customer. Note the constructor order: name first, then document.
    private static Customer mapRow(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getString("full_name"),
                rs.getString("document_number"),
                rs.getString("phone"),
                rs.getString("email"));
    }

    // Same rule as Customer: ignore surrounding spaces in the document number.
    private static String normalize(String documentNumber) {
        return documentNumber == null ? null : documentNumber.strip();
    }
}
