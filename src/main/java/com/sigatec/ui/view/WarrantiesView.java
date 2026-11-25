package com.sigatec.ui.view;

import com.sigatec.model.DomainException;
import com.sigatec.model.Warranty;
import com.sigatec.service.WarrantyService;
import com.sigatec.ui.Screen;
import com.sigatec.ui.UiSupport;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

/**
 *  Warranties screen: register warranties and list them with their validity.
 */
public final class WarrantiesView implements Screen {
    private static final String ALL = "Todas";
    private static final String ACTIVE = "Vigentes";
    private static final String EXPIRED = "Vencidas";

    private final WarrantyService warranties;

    private final TextField serialField = new TextField();
    private final TextField documentField = new TextField();
    private final DatePicker purchaseDate = new DatePicker(LocalDate.now());
    private final TextField monthsField = new TextField("12");
    private final ComboBox<String> statusFilter = new ComboBox<>();
    private final TableView<Warranty> table = new TableView<>();

    public WarrantiesView(WarrantyService warranties) {
        this.warranties = warranties;
    }

    @Override
    public Parent build() {
        // Title and subtitle
        Label title = new Label("Garantías");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Garantías registradas y su vigencia");
        subtitle.getStyleClass().add("page-subtitle");

        Button registerButton = new Button("Registrar garantia");
        registerButton.getStyleClass().add("primary-button");
        registerButton.setOnAction(e -> register());

        GridPane form = new GridPane();
        form.setHgap(14);
        form.setVgap(10);
        form.addRow(0, new Label("Serie del producto"), serialField);
        form.addRow(1, new Label("Documento del cliente"), documentField);
        form.addRow(2, new Label("Fecha de compra"), purchaseDate);
        form.addRow(3, new Label("Meses de cobertura"), monthsField);
        GridPane.setHgrow(serialField, Priority.ALWAYS);
        GridPane.setHgrow(documentField, Priority.ALWAYS);

        VBox formCard = new VBox(12, form, registerButton);
        formCard.getStyleClass().add("card");

        // Filter: show all, only active, or only expired warranties
        statusFilter.getItems().setAll(ALL, ACTIVE, EXPIRED);
        statusFilter.setValue(ALL);
        statusFilter.valueProperty().addListener((observable, oldValue, newValue) -> refresh());
        HBox filterBar = new HBox(8, new Label("Mostrar:"), statusFilter);

        buildTable();

        // Main container: everything from top to bottom
        VBox root = new VBox(20, title, subtitle, formCard, filterBar, table);
        root.setPadding(new Insets(28, 32, 28, 32));
        VBox.setVgrow(table, Priority.ALWAYS);

        refresh();
        return root;
    }

    @Override
    public void refresh() {
        LocalDate today = LocalDate.now();
        String filter = statusFilter.getValue() == null ? ALL : statusFilter.getValue();
        table.getItems().setAll(warranties.listAll().stream()
                .filter(w -> ALL.equals(filter) || ACTIVE.equals(filter) == w.isActive(today))
                .toList());
    }

    private void register(){
        UiSupport.run(() -> {
            int months = parseMonths();
            Warranty warranty = warranties.register(serialField.getText(), documentField.getText(),
                    purchaseDate.getValue(), months);
            serialField.clear();
            documentField.clear();
            refresh();
            UiSupport.showInfo("Garantia registrada con el ID " + warranty.getId() + ".");
        });
    }

    /**
     * Converts the months text to a whole number, with a user-facing error message.
     */
    private int parseMonths(){
        try {
            return Integer.parseInt(monthsField.getText().trim());

        }catch (NumberFormatException e){
            throw new DomainException("Los meses de cobertura deben ser un numero entero.");
        }
    }

    private void buildTable() {
        table.getColumns().add(UiSupport.<Warranty>textColumn("ID garantía", Warranty::getId));
        table.getColumns().add(UiSupport.<Warranty>textColumn("Producto", w -> w.getProduct().getName()));
        table.getColumns().add(UiSupport.<Warranty>textColumn("Serie", w -> w.getProduct().getSerialNumber()));
        table.getColumns().add(UiSupport.<Warranty>textColumn("Cliente", w -> w.getCustomer().getFullName()));
        table.getColumns().add(UiSupport.<Warranty>textColumn("Documento", w -> w.getCustomer().getDocumentNumber()));
        table.getColumns().add(UiSupport.<Warranty>textColumn("Compra", w -> w.getPurchaseDate().toString()));
        table.getColumns().add(UiSupport.<Warranty>textColumn("Meses", w -> String.valueOf(w.getCoverageMonths())));
        table.getColumns().add(UiSupport.<Warranty>textColumn("Vence", w -> w.getExpirationDate().toString()));
        table.getColumns().add(UiSupport.<Warranty>textColumn("Estado",
                w -> w.statusOn(LocalDate.now()).getLabel()));
        table.setPlaceholder(new Label("No hay garantías que mostrar."));
    }
}
