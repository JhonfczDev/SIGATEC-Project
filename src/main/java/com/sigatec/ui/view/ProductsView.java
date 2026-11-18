package com.sigatec.ui.view;

import com.sigatec.model.Product;
import com.sigatec.service.ProductService;
import com.sigatec.service.RemovalService;
import com.sigatec.ui.Screen;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField

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
        //it will be completed in a moment...
        return null;
    }

    @Override
    public void refresh() {
        //it will be completed in a moment...
    }
}
