package com.sigatec.repository.jdbc;

import com.sigatec.model.Product;
import com.sigatec.repository.PersistenceException;
import com.sigatec.repository.ProductRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class JdbcProductRepository implements ProductRepository {
    // "?" marks a value that is filled in later, so user input never becomes part of the SQL text.
    private static final String UPSERT = """
            INSERT INTO products (serial_number, name, brand, model, price)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (serial_number) DO UPDATE SET
                name = EXCLUDED.name,
                brand = EXCLUDED.brand,
                model = EXCLUDED.model,
                price = EXCLUDED.price
            """;

    private static final String SELECT_BY_ID =
            "SELECT serial_number, name, brand, model, price FROM products WHERE serial_number = ?";

    private static final String SELECT_ALL =
            "SELECT serial_number, name, brand, model, price FROM products ORDER BY serial_number";

    private static final String EXISTS =
            "SELECT 1 FROM products WHERE serial_number = ?";

    private static final String DELETE =
            "DELETE FROM products WHERE serial_number = ?";

    @Override
    public Optional<Product> findBySerialNumber(String serialNumber) {
        return findById(serialNumber);
    }

    @Override
    public void save(Product product) {
        try(Connection connection = Database.getConnection();
            PreparedStatement statement = connection.prepareStatement(UPSERT)) {

            statement.setString(1, product.getSerialNumber());
            statement.setString(2, product.getName());
            statement.setString(3, product.getBrand());
            statement.setString(4, product.getModel());
            statement.setBigDecimal(5, product.getPrice());
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new PersistenceException("No se pudo guardar el producto", e);
        }

    }

    @Override
    public Optional<Product> findById(String id) {
        try(Connection connection = Database.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID)) {
            statement.setString(1, normalize(id));

            try(ResultSet rs = statement.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenceException("No se pudo buscar el producto.", e);
        }
    }

    @Override
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();

        try(Connection connection = Database.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_ALL);
            ResultSet rs = statement.executeQuery()){

            while (rs.next()){
                products.add(mapRow(rs));
            }
        }catch (SQLException e){
            throw new PersistenceException("No se pudieron cargar los productos.", e);
        }

        return products;
    }

    @Override
    public boolean existsById(String id) {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(EXISTS)) {
            statement.setString(1, normalize(id));

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new PersistenceException("No se pudo verificar el producto.", e);
        }
    }

    @Override
    public boolean deleteById(String id) {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE)) {
            statement.setString(1, normalize(id));

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            // Fails if a warranty still uses this product (foreign key rule).
            throw new PersistenceException("No se pudo eliminar el producto.", e);
        }
    }

    // Converts the current row of the result into a Product object.
    private static Product mapRow(ResultSet rs) throws  SQLException {
        return new Product(
                rs.getString("name"),
                rs.getString("brand"),
                rs.getString("model"),
                rs.getString("serial_number"),
                rs.getBigDecimal("price"));
    }

    // Same rule as Product: serial numbers are stored in uppercase, without surrounding spaces.
    private static String normalize(String serial){
        return serial == null ? null : serial.strip().toUpperCase();
    }
}
