package com.sigatec.ui.view;

import com.sigatec.model.DomainException;
import com.sigatec.model.Product;
import com.sigatec.service.ProductService;
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

import java.math.BigDecimal;

public class ProductsView implements Screen {
    private final ProductService products;
    private final RemovalService removal;

    private final TextField serialField = new TextField();
    private final TextField nameField = new TextField();
    private final TextField brandField = new TextField();
    private final TextField modelField = new TextField();
    private final TextField priceField = new TextField();
    private final TextField searchField = new TextField();
    private final TableView<Product> table = new TableView<>();
    private final Button saveButton = new Button("Registrar");
    private final Button deleteButton = new Button("Eliminar");

    /**
     * The product currently being edited; null when the form is creating a new one.
     */
    private Product selected;

    public ProductsView(ProductService products, RemovalService removal) {
        this.products = products;
        this.removal = removal;
    }

    @Override
    public Parent build() {
        //Title and Subtitle
        Label title = new Label("Productos");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Catalogo de productos Registrados");
        subtitle.getStyleClass().add("page-subtitle");

        //Search: filter the table as the user types
        searchField.setPromptText("Buscar por Nombre, Marca o Modelo");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> refresh());

        //Table columns
        buildTable();

        //Selecting a row loads it into the form
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, product) -> fill(product));

        //Buttons
        saveButton.getStyleClass().add("primary-button");
        saveButton.setOnAction(e -> save());
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setOnAction(e -> delete());
        Button newButton = new Button("Nuevo");
        newButton.setOnAction(e ->{
            table.getSelectionModel().clearSelection();
            fill(null);
        });

        HBox actions = new HBox(8, saveButton, newButton, deleteButton);

        //Form
        GridPane form = new GridPane();
        form.setHgap(14);
        form.setVgap(10);
        form.addRow(0, new Label("Numero de Serie"), serialField);
        form.addRow(1, new Label("Nombre"), nameField);
        form.addRow(2, new Label("Marca"), brandField);
        form.addRow(3, new Label("Modelo"), modelField);
        form.addRow(4, new Label("Precio"), priceField);
        GridPane.setHgrow(serialField, Priority.ALWAYS);
        GridPane.setHgrow(nameField, Priority.ALWAYS);
        GridPane.setHgrow(brandField, Priority.ALWAYS);
        GridPane.setHgrow(modelField, Priority.ALWAYS);
        GridPane.setHgrow(priceField,Priority.ALWAYS);

        VBox formCard = new VBox(12, form, actions);
        formCard.getStyleClass().add("card");

        //Main container: everything from top to bottom
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
        table.getItems().setAll(text == null || text.isBlank() ? products.listAll() : products.search(text));
    }

    /**
     * Loads a product into the form, or clears the form when product is null.
     */
    private void fill(Product product){
        selected = product;
        boolean editing = product != null;

        if (editing){
            serialField.setText(product.getSerialNumber());
            nameField.setText(product.getName());
            brandField.setText(product.getBrand());
            modelField.setText(product.getModel());
            priceField.setText(product.getPrice().toPlainString());
        }else {
            serialField.clear();
            nameField.clear();
            brandField.clear();
            modelField.clear();
            priceField.clear();
        }
        serialField.setDisable(editing); //The serial number cannot change
        saveButton.setText(editing ? "Guardar Cambios" : "Registrar");
        deleteButton.setDisable(!editing);
    }

    private void save(){
        UiSupport.run(()->{
           BigDecimal price = parsePrice();

           if (selected == null){
               products.register(nameField.getText(), brandField.getText(), modelField.getText(), serialField.getText(), price);
           }else {
               products.update(selected.getSerialNumber(), nameField.getText(), brandField.getText(), modelField.getText(), price);
           }
           table.getSelectionModel().clearSelection();
           fill(null);
           refresh();
        });
    }

    private void delete() {
        if (selected == null || !UiSupport.confirm("¿Eliminar el producto " + selected.getName() + "?")) {
            return;
        }
        UiSupport.run(() -> {
            removal.deleteProdut(selected.getSerialNumber());
            table.getSelectionModel().clearSelection();
            fill(null);
            refresh();
        });
    }

    /** Converts the price text to a number, with a user-facing error message. */
    private BigDecimal parsePrice() {
        try {
            return new BigDecimal(priceField.getText().trim());
        } catch (NumberFormatException e) {
            throw new DomainException("El precio debe ser un número válido.");
        }
    }

    private void buildTable() {
        table.getColumns().add(UiSupport.<Product>textColumn("Número de serie", Product::getSerialNumber));
        table.getColumns().add(UiSupport.<Product>textColumn("Nombre", Product::getName));
        table.getColumns().add(UiSupport.<Product>textColumn("Marca", Product::getBrand));
        table.getColumns().add(UiSupport.<Product>textColumn("Modelo", Product::getModel));
        table.getColumns().add(UiSupport.<Product>textColumn("Precio", p -> p.getPrice().toPlainString()));
        table.setPlaceholder(new Label("No hay productos que mostrar."));
    }

}
