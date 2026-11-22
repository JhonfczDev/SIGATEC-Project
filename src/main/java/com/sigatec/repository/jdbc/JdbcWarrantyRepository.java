package com.sigatec.repository.jdbc;

import com.sigatec.model.Customer;
import com.sigatec.model.Product;
import com.sigatec.model.Warranty;
import com.sigatec.repository.CustomerRepository;
import com.sigatec.repository.PersistenceException;
import com.sigatec.repository.ProductRepository;
import com.sigatec.repository.WarrantyRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class JdbcWarrantyRepository implements WarrantyRepository {

    // "?" marks a value that is filled in later, so user input never becomes part of the SQL text.
    private static final String UPSERT = """
            INSERT INTO warranties (id, product_serial, customer_document, purchase_date, coverage_months)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                product_serial = EXCLUDED.product_serial,
                customer_document = EXCLUDED.customer_document,
                purchase_date = EXCLUDED.purchase_date,
                coverage_months = EXCLUDED.coverage_months
            """;

    private static final String SELECT_BY_ID =
            "SELECT id, product_serial, customer_document, purchase_date, coverage_months FROM warranties WHERE id = ?";

    private static final String SELECT_ALL =
            "SELECT id, product_serial, customer_document, purchase_date, coverage_months FROM warranties ORDER BY id";

    private static final String SELECT_BY_PRODUCT =
            "SELECT id, product_serial, customer_document, purchase_date, coverage_months FROM warranties WHERE product_serial = ? ORDER BY id";

    private static final String SELECT_BY_CUSTOMER =
            "SELECT id, product_serial, customer_document, purchase_date, coverage_months FROM warranties WHERE customer_document = ? ORDER BY id";

    private static final String EXISTS =
            "SELECT 1 FROM warranties WHERE id = ?";

    private static final String DELETE =
            "DELETE FROM warranties WHERE id = ?";

    // Takes the highest number already used (GAR-0009 -> 9), so the next one can be 10.
    private static final String MAX_NUMBER =
            "SELECT COALESCE(MAX(CAST(SUBSTRING(id FROM 5) AS INT)), 0) FROM warranties";

    private final ProductRepository products;
    private final CustomerRepository customers;

    public JdbcWarrantyRepository(ProductRepository products, CustomerRepository customers) {
        this.products = products;
        this.customers = customers;
    }

    @Override
    public String nextId() {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(MAX_NUMBER);
             ResultSet rs = statement.executeQuery()) {
            rs.next();
            return String.format("GAR-%04d", rs.getInt(1) + 1);
        } catch (SQLException e) {
            throw new PersistenceException("No se pudo generar el número de la garantía.", e);
        }
    }

    @Override
    public List<Warranty> findByProductSerial(String serialNumber) {
        return query(SELECT_BY_PRODUCT, serialNumber, "No se pudieron buscar las garantías del producto.");
    }

    @Override
    public List<Warranty> findByCustomerDocument(String documentNumber) {
        return query(SELECT_BY_CUSTOMER, documentNumber, "No se pudieron buscar las garantías del cliente.");
    }

    @Override
    public void save(Warranty warranty) {
        try(Connection connection = Database.getConnection();
            PreparedStatement statement = connection.prepareStatement(UPSERT)) {

            statement.setString(1, warranty.getId());
            statement.setString(2, warranty.getProduct().getSerialNumber());
            statement.setString(3, warranty.getCustomer().getDocumentNumber());
            statement.setDate(4, Date.valueOf(warranty.getPurchaseDate()));
            statement.setInt(5, warranty.getCoverageMonths());
            statement.executeUpdate();

        }catch (SQLException e){
            throw new PersistenceException("No se pudo guardar la garantia.", e);
        }
    }

    @Override
    public Optional<Warranty> findById(String id) {
        try(Connection connection = Database.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID)) {
            statement.setString(1, id);

            try(ResultSet rs = statement.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }catch (SQLException e){
            throw new PersistenceException("No se pudo buscar la garantia", e);
        }
    }

    @Override
    public List<Warranty> findAll() {
        return query(SELECT_ALL, null, "No se pudieron cargar las garantias.");
    }

    @Override
    public boolean existsById(String id) {
        try(Connection connection = Database.getConnection();
            PreparedStatement statement = connection.prepareStatement(EXISTS)) {
            statement.setString(1, id);

            try(ResultSet rs = statement.executeQuery()) {
             return rs.next();
            }
        }catch (SQLException e){
            throw new PersistenceException("No se pudo verificar la garantia.", e);
        }
    }

    @Override
    public boolean deleteById(String id) {
        try(Connection connection = Database.getConnection();
            PreparedStatement statement = connection.prepareStatement(DELETE)){

            statement.setString(1, id);
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new PersistenceException("No se pudo eliminar la garantia", e);
        }
    }

    // Runs a query that may take one parameter or none, and converts every row.
    private List<Warranty> query(String sql, String parameter, String errorMessage) {
        List<Warranty> result = new ArrayList<>();
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

    // Rebuilds a Warranty: the product and the customer are looked up in their own repositories.
    private Warranty mapRow(ResultSet rs) throws SQLException {
        String productSerial = rs.getString("product_serial");
        String customerDocument = rs.getString("customer_document");

        Product product = products.findBySerialNumber(productSerial)
                .orElseThrow(() -> new PersistenceException("Falta el producto " + productSerial));
        Customer customer = customers.findByDocumentNumber(customerDocument)
                .orElseThrow(() -> new PersistenceException("Falta el cliente " + customerDocument));

        return new Warranty(
                rs.getString("id"),
                product,
                customer,
                rs.getDate("purchase_date").toLocalDate(),
                rs.getInt("coverage_months"));
    }
}
