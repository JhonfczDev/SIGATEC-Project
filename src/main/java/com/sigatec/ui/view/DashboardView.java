package com.sigatec.ui.view;

import com.sigatec.model.Warranty;
import com.sigatec.service.ProductService;
import com.sigatec.service.ReportService;
import com.sigatec.ui.Screen;
import com.sigatec.ui.UiSupport;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;

/** Main dashboard: summary cards and the warranties that expire soon. */
public final class DashboardView implements Screen {

    private static final int EXPIRING_WITHIN_DAYS = 30;

    private final ReportService reports;
    private final ProductService products;

    private final Label activeValue = new Label();
    private final Label expiringValue = new Label();
    private final Label pendingValue = new Label();
    private final Label productsValue = new Label();
    private final TableView<Warranty> expiringTable = new TableView<>();

    public DashboardView(ReportService reports, ProductService products) {
        this.reports = reports;
        this.products = products;
    }

    @Override
    public Parent build() {
        Label title = UiSupport.pageTitle("Panel principal");
        Label subtitle = UiSupport.pageSubtitle("Resumen de garantías y reclamaciones");

        // Summary cards
        HBox kpis = new HBox(16,
                kpiCard("Garantías vigentes", activeValue),
                kpiCard("Vencen en 30 días", expiringValue),
                kpiCard("Reclamaciones pendientes", pendingValue),
                kpiCard("Productos registrados", productsValue));
        for (Node card : kpis.getChildren()) {
            HBox.setHgrow(card, Priority.ALWAYS);
        }

        buildExpiringTable();

        // Main container: everything from top to bottom
        VBox root = new VBox(20,
                title,
                subtitle,
                kpis,
                UiSupport.sectionTitle("Garantías próximas a vencer"),
                expiringTable);
        root.setPadding(new Insets(28, 32, 28, 32));
        VBox.setVgrow(expiringTable, Priority.ALWAYS);

        refresh();
        return root;
    }

    @Override
    public void refresh() {
        activeValue.setText(String.valueOf(reports.activeWarranties().size()));
        List<Warranty> expiring = reports.warrantiesExpiringWithin(EXPIRING_WITHIN_DAYS);
        expiringValue.setText(String.valueOf(expiring.size()));
        pendingValue.setText(String.valueOf(reports.pendingClaims().size()));
        productsValue.setText(String.valueOf(products.listAll().size()));
        expiringTable.getItems().setAll(expiring);
    }

    /** A white card with a caption and a big number. */
    private static VBox kpiCard(String caption, Label value) {
        Label text = new Label(caption);
        text.getStyleClass().add("kpi-caption");
        value.getStyleClass().add("kpi-value");
        return UiSupport.card(text, value);
    }

    private void buildExpiringTable() {
        expiringTable.getColumns().add(UiSupport.<Warranty>textColumn("ID garantía", Warranty::getId));
        expiringTable.getColumns().add(UiSupport.<Warranty>textColumn("Producto", w -> w.getProduct().getName()));
        expiringTable.getColumns().add(UiSupport.<Warranty>textColumn("Serie", w -> w.getProduct().getSerialNumber()));
        expiringTable.getColumns().add(UiSupport.<Warranty>textColumn("Cliente", w -> w.getCustomer().getFullName()));
        expiringTable.getColumns().add(UiSupport.<Warranty>textColumn("Vence", w -> w.getExpirationDate().toString()));
        expiringTable.getColumns().add(UiSupport.<Warranty>textColumn("Días",
                w -> String.valueOf(w.remainingDays(LocalDate.now()))));
        expiringTable.setPlaceholder(new Label("No hay garantías por vencer en los próximos 30 días."));
    }
}