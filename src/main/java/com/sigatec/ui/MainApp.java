package com.sigatec.ui;

import com.sigatec.repository.file.FileStorage;
import com.sigatec.service.ClaimService;
import com.sigatec.service.CustomerService;
import com.sigatec.service.ProductService;
import com.sigatec.service.RemovalService;
import com.sigatec.service.WarrantyService;
import com.sigatec.ui.view.ProductsView;
import com.sigatec.ui.view.CustomerView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDate;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        // Data lives in the user's folder, so it survives updates.
        Path dataFolder = Path.of(System.getProperty("user.home"), "SIGATEC", "data");
        FileStorage storage = FileStorage.open(dataFolder, LocalDate.now());
        Clock clock = Clock.systemDefaultZone();

        // Services, created in the same order as in Main.
        ProductService products = new ProductService(storage.products());
        CustomerService customers = new CustomerService(storage.customers());
        WarrantyService warranties = new WarrantyService(storage.warranties(), products, customers, clock);
        ClaimService claims = new ClaimService(storage.claims(), warranties, clock);
        RemovalService removal = new RemovalService(products, customers, warranties);

        // Screens are built once, then shown when the user clicks their button.
        Parent dashboardNode = new DashboardView().build();
        ProductsView productsView = new ProductsView(products, removal);
        Parent productsNode = productsView.build();
        CustomerView customersView = new CustomerView(customers, removal);
        Parent customersNode = customersView.build();

        BorderPane root = new BorderPane();
        root.setCenter(dashboardNode);

        Button dashboardButton = new Button("Panel principal");
        dashboardButton.setMaxWidth(Double.MAX_VALUE);
        dashboardButton.setOnAction(e -> root.setCenter(dashboardNode));

        Button productsButton = new Button("Productos");
        productsButton.setMaxWidth(Double.MAX_VALUE);
        productsButton.setOnAction(e -> {
            root.setCenter(productsNode);
            productsView.refresh();
        });

        Button customersButton = new Button("Clientes");
        customersButton.setMaxWidth(Double.MAX_VALUE);
        customersButton.setOnAction(e -> {
            root.setCenter(customersNode);
            customersView.refresh();
        });

        Label brand = new Label("SIGATEC");
        brand.getStyleClass().add("brand");

        VBox sidebar = new VBox(8, brand, dashboardButton, productsButton, customersButton);
        sidebar.setPadding(new Insets(24, 16, 24, 16));
        sidebar.setPrefWidth(220);
        root.setLeft(sidebar);

        Scene scene = new Scene(root, 1280, 800);
        scene.getStylesheets().add(MainApp.class.getResource("app.css").toExternalForm());
        stage.setTitle("SIGATEC");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}