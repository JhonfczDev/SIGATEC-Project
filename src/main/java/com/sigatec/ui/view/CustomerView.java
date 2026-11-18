package com.sigatec.ui.view;

import com.sigatec.model.Customer;
import com.sigatec.service.CustomerService;
import com.sigatec.service.RemovalService;
import com.sigatec.ui.Screen;
import com.sigatec.ui.UiSupport;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class CustomerView implements Screen {
    private final CustomerService customers;
    private final RemovalService removal;

    private final TextField documentField = new TextField();
    private final TextField nameField = new TextField();
    private final TextField phoneField = new TextField();
    private final TextField emailField = new TextField();
    private final TextField searchField = new TextField();
    private final TableView<Customer> table = new TableView<>();
    private final Button saveButton = new Button("Registrar");
    private final Button deleteButton = new Button("Eliminar");

    /**
     * Customer being edited; null when the form registers a new one.
     */
    private Customer selected;

    public CustomerView(CustomerService customers, RemovalService removal) {
        this.customers = customers;
        this.removal = removal;
    }

    @Override
    public Parent build() {
        // title and subtitle
        Label title = new Label("Clientes");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Pwersonas que compran productos y tienen gsrsntias");
        subtitle.getStyleClass().add("page-subtitle");

        // Search: filter the table by name as the user types
        searchField.setPromptText("Buscar por nombre");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> refresh());

        // Table columns
        buildTable();

        // Selecting a row loads it into the form
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, customer) -> fill(customer));

        // Buttons
        saveButton.getStyleClass().add("primary-button");
        saveButton.setOnAction(e -> save());
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setOnAction(e -> delete());
        Button newButton = new Button("Nuevo");
        newButton.setOnAction(e -> {
            table.getSelectionModel().clearSelection();
            fill(null);
        });
        HBox actions = new HBox(8, saveButton, newButton, deleteButton);

        // Form
        GridPane form = new GridPane();
        form.setHgap(14);
        form.setVgap(10);
        form.addRow(0, new Label("Documento"), documentField);
        form.addRow(1, new Label("Nombre completo"), nameField);
        form.addRow(2, new Label("Teléfono"), phoneField);
        form.addRow(3, new Label("Correo"), emailField);
        GridPane.setHgrow(documentField, Priority.ALWAYS);
        GridPane.setHgrow(nameField, Priority.ALWAYS);
        GridPane.setHgrow(phoneField, Priority.ALWAYS);
        GridPane.setHgrow(emailField, Priority.ALWAYS);

        VBox formCard = new VBox(12, form, actions);
        formCard.getStyleClass().add("card");

        // Main container: everything from top to bottom
        VBox root = new VBox(20, title, subtitle, formCard, searchField, table);
        root.setPadding(new Insets(28, 32, 28, 32));
        VBox.setVgrow(table, Priority.ALWAYS);

        fill(null);
        refresh();
        return root;
    }

    @Override
    public void refresh() {
        String text = searchField.getText();
        table.getItems().setAll(text == null || text.isBlank() ? customers.listAll() : customers.searchByName(text));
    }

    /** Loads a customer into the form, or clears the form when customer is null. */
    private void fill(Customer customer) {
        selected = customer;
        boolean editing = customer != null;

        if (editing) {
            documentField.setText(customer.getDocumentNumber());
            nameField.setText(customer.getFullName());
            phoneField.setText(customer.getPhone());
            emailField.setText(customer.getEmail());
        } else {
            documentField.clear();
            nameField.clear();
            phoneField.clear();
            emailField.clear();
        }
        documentField.setDisable(editing); // the document number cannot change
        saveButton.setText(editing ? "Guardar cambios" : "Registrar");
        deleteButton.setDisable(!editing);
    }

    private void save() {
        UiSupport.run(() -> {
            if (selected == null) {
                customers.register(nameField.getText(), documentField.getText(),
                        phoneField.getText(), emailField.getText());
            } else {
                customers.update(selected.getDocumentNumber(), nameField.getText(),
                        phoneField.getText(), emailField.getText());
            }
            table.getSelectionModel().clearSelection();
            fill(null);
            refresh();
        });
    }

    private void delete() {
        if (selected == null || !UiSupport.confirm("¿Eliminar al cliente " + selected.getFullName() + "?")) {
            return;
        }
        UiSupport.run(() -> {
            removal.deleteCustomer(selected.getDocumentNumber());
            table.getSelectionModel().clearSelection();
            fill(null);
            refresh();
        });
    }

    private void buildTable() {
        table.getColumns().add(UiSupport.<Customer>textColumn("Documento", Customer::getDocumentNumber));
        table.getColumns().add(UiSupport.<Customer>textColumn("Nombre completo", Customer::getFullName));
        table.getColumns().add(UiSupport.<Customer>textColumn("Teléfono", Customer::getPhone));
        table.getColumns().add(UiSupport.<Customer>textColumn("Correo", Customer::getEmail));
        table.setPlaceholder(new Label("No hay clientes que mostrar."));
    }
}
