package com.sigatec.ui;

import com.sigatec.repository.file.FileStorage;
import com.sigatec.service.*;
import com.sigatec.ui.view.*;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
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
        ReportService reports = new ReportService(warranties, claims);
        RemovalService removal = new RemovalService(products, customers, warranties);

        // Screens are built once, then shown when the user clicks their button.
        DashboardView dashboardView = new DashboardView(reports, products);
        Parent dashboardNode = dashboardView.build();
        ProductsView productsView = new ProductsView(products, removal);
        Parent productsNode = productsView.build();
        CustomersView customersView = new CustomersView(customers, removal);
        Parent customersNode = customersView.build();
        WarrantiesView warrantiesView = new WarrantiesView(warranties);
        Parent warrantiesNode = warrantiesView.build();
        ClaimsView claimsView = new ClaimsView(claims);
        Parent claimsNode = claimsView.build();
        ReportsView reportsView = new ReportsView(reports);
        Parent reportsNode = reportsView.build();

        BorderPane root = new BorderPane();
        root.getStyleClass().add("content");
        root.setCenter(dashboardNode);

        Text siga = new Text("SIGA");
        siga.getStyleClass().add("brand-siga");
        Text tec = new Text("TEC");
        tec.getStyleClass().add("brand-tec");
        TextFlow brand = new TextFlow(siga, tec);

        VBox sidebar = new VBox(8,
                brand,
                navButton("Panel principal", dashboardNode, dashboardView, root),
                navButton("Productos", productsNode, productsView, root),
                navButton("Clientes", customersNode, customersView, root),
                navButton("Garantías", warrantiesNode, warrantiesView, root),
                navButton("Reclamaciones", claimsNode, claimsView, root),
                navButton("Reportes", reportsNode, reportsView, root));
        sidebar.getStyleClass().add("sidebar");
        root.setLeft(sidebar);

        Scene scene = new Scene(root, 1280, 800);
        scene.getStylesheets().add(MainApp.class.getResource("app.css").toExternalForm());
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("icon.png")));
        stage.setTitle("SIGATEC");
        stage.setScene(scene);
        stage.show();
    }

    /** A sidebar button: clicking it shows the screen in the center and refreshes its data. */
    private static Button navButton(String text, Parent node, Screen screen, BorderPane root) {
        Button button = new Button(text);
        button.getStyleClass().add("nav-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> {
            root.setCenter(node);
            screen.refresh();
        });
        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}