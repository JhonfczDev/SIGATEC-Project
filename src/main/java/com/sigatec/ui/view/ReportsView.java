package com.sigatec.ui.view;

import com.sigatec.model.Claim;
import com.sigatec.model.DomainException;
import com.sigatec.model.Warranty;
import com.sigatec.service.ReportService;
import com.sigatec.ui.Screen;
import com.sigatec.ui.UiSupport;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

/** Reports screen: full lists of active, expiring and pending items. */
public final class ReportsView implements Screen {
    private final ReportService reports;

    private final TextField daysField = new TextField("30");
    private final TableView<Warranty> activeTable = new TableView<>();
    private final TableView<Warranty> expiringTable = new TableView<>();
    private final TableView<Claim> pendingTable = new TableView<>();

    public ReportsView(ReportService reports) {
        this.reports = reports;
    }

    @Override
    public Parent build() {
        // Title and subtitle
        Label title = new Label("Reportes");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Listas completas de garantías y reclamaciones");
        subtitle.getStyleClass().add("page-subtitle");

        // Controls: number of days to look ahead for expiring warranties
        daysField.setPrefWidth(80);
        Button refreshButton = new Button("Actualizar");
        refreshButton.setOnAction(e -> refresh());
        HBox controls = new HBox(8, new Label("Días para vencer:"), daysField, refreshButton);

        // Tables
        buildWarrantyTable(activeTable);
        buildWarrantyTable(expiringTable);
        buildClaimTable(pendingTable);

        // Main container: sections from top to bottom, inside a scroll area
        VBox root = new VBox(16,
                title,
                subtitle,
                controls,
                UiSupport.sectionTitle("Garantías vigentes"),
                activeTable,
                UiSupport.sectionTitle("Garantías próximas a vencer"),
                expiringTable,
                UiSupport.sectionTitle("Reclamaciones pendientes"),
                pendingTable);
        root.setPadding(new Insets(28, 32, 28, 32));

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);

        refresh();
        return scroll;
    }

    @Override
    public void refresh() {
        UiSupport.run(() -> {
            int days = parseDays();
            activeTable.getItems().setAll(reports.activeWarranties());
            expiringTable.getItems().setAll(reports.warrantiesExpiringWithin(days));
            pendingTable.getItems().setAll(reports.pendingClaims());
        });
    }

    /** Converts the days text to a whole number, with a user-facing error message. */
    private int parseDays() {
        try {
            return Integer.parseInt(daysField.getText().trim());
        } catch (NumberFormatException e) {
            throw new DomainException("Los días deben ser un número entero.");
        }
    }

    private static void buildWarrantyTable(TableView<Warranty> table) {
        table.getColumns().add(UiSupport.<Warranty>textColumn("ID garantía", Warranty::getId));
        table.getColumns().add(UiSupport.<Warranty>textColumn("Producto", w -> w.getProduct().getName()));
        table.getColumns().add(UiSupport.<Warranty>textColumn("Serie", w -> w.getProduct().getSerialNumber()));
        table.getColumns().add(UiSupport.<Warranty>textColumn("Cliente", w -> w.getCustomer().getFullName()));
        table.getColumns().add(UiSupport.<Warranty>textColumn("Vence", w -> w.getExpirationDate().toString()));
        table.getColumns().add(UiSupport.<Warranty>textColumn("Días restantes",
                w -> String.valueOf(w.remainingDays(LocalDate.now()))));
        table.setPlaceholder(new Label("Sin registros."));
        table.setPrefHeight(220);
    }

    private static void buildClaimTable(TableView<Claim> table) {
        table.getColumns().add(UiSupport.<Claim>textColumn("ID reclamación", Claim::getId));
        table.getColumns().add(UiSupport.<Claim>textColumn("Garantía", c -> c.getWarranty().getId()));
        table.getColumns().add(UiSupport.<Claim>textColumn("Problema", Claim::getDescription));
        table.getColumns().add(UiSupport.<Claim>textColumn("Fecha", c -> c.getClaimDate().toString()));
        table.getColumns().add(UiSupport.<Claim>textColumn("Estado", c -> c.getStatus().getLabel()));
        table.setPlaceholder(new Label("Sin registros."));
        table.setPrefHeight(220);
    }
}
