package com.sigatec.ui.view;

import com.sigatec.model.Claim;
import com.sigatec.model.ClaimStatus;
import com.sigatec.service.ClaimService;
import com.sigatec.ui.Screen;
import com.sigatec.ui.UiSupport;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.LocalDate;

public final class ClaimsView implements Screen {
    private final ClaimService claims;

    private final ToggleGroup filters = new ToggleGroup();
    private final TextField warrantyField = new TextField();
    private final TextField descriptionField = new TextField();
    private final DatePicker dateField = new DatePicker(LocalDate.now());
    private final ComboBox<ClaimStatus> newStatus = new ComboBox<>();
    private final TableView<Claim> table = new TableView<>();

    public ClaimsView(ClaimService claims) {
        this.claims = claims;
    }

    @Override
    public Parent build() {
        // Title and subtitle
        Label title = new Label("Reclamaciones");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Seguimiento de reclamos de garantía");
        subtitle.getStyleClass().add("page-subtitle");

        buildTable();

        // Keep exactly one filter selected
        filters.selectedToggleProperty().addListener((observable, previus, current) ->{
            if (current == null){
                previus.setSelected(true);

            }else {
                refresh();
            }
        });

        // Main container: everything from top bottom
        VBox root = new VBox(20,title, subtitle, buildForm(), buildFilterBar(), buildStatusBar(), table);
        root.setPadding(new Insets(28, 32, 28, 32));
        VBox.setVgrow(table, Priority.ALWAYS);

        refresh();
        return root;
    }

    @Override
    public void refresh() {
        ClaimStatus filter = (ClaimStatus) filters.getSelectedToggle().getUserData();
        table.getItems().setAll(claims.listAll().stream().filter(c -> filter == null || c.getStatus() == filter).toList());
    }

    /**
     * Form to file a new claim
     */
    private Node buildForm() {
        warrantyField.setPromptText("GAR-0001");
        descriptionField.setPromptText("Ej.: no enciende después de una caída");

        Button saveButton = new Button("Guardar");
        saveButton.getStyleClass().add("primary-button");
        saveButton.setOnAction(e -> file());

        GridPane form = new GridPane();
        form.setHgap(14);
        form.setVgap(6);
        form.addRow(0,
                new Label("ID de garantía"),
                new Label("Descripción del problema"),
                new Label("Fecha de reclamo"));
        form.addRow(1, warrantyField, descriptionField, dateField, saveButton);
        GridPane.setHgrow(descriptionField, Priority.ALWAYS);

        VBox card = new VBox(12, new Label("Registrar reclamación"), form);
        card.getStyleClass().add("card");
        return card;
    }

    /**
     *  Files a claim using the form values. The service validates the warranty and the date.
     */
    private void file() {
        UiSupport.run(() -> {
            claims.file(warrantyField.getText().trim(), descriptionField.getText(), dateField.getValue(), null);
            warrantyField.clear();
            descriptionField.clear();
            refresh();
        });
    }

    /**
     *  Status filter buttons. The "Todas" button uses null as its value.
     */
    private Node buildFilterBar() {
        return new HBox(8,
                filterButton("Todas", null),
                filterButton("Pendientes", ClaimStatus.PENDING),
                filterButton("En reparación", ClaimStatus.IN_REPAIR),
                filterButton("Resueltas", ClaimStatus.RESOLVED),
                filterButton("Rechazadas", ClaimStatus.REJECTED));
    }

    private ToggleButton filterButton(String text, ClaimStatus status) {
        ToggleButton button = new ToggleButton(text);
        button.getStyleClass().add("chip");
        button.setToggleGroup(filters);
        button.setUserData(status);
        if (status == null) {
            button.setSelected(true);
        }
        return button;
    }

    /**
     *  Dropdown and button to change the status of the selected claim.
     */
    private Node buildStatusBar() {
        newStatus.getItems().setAll(ClaimStatus.values());
        newStatus.setConverter(new StringConverter<>() {
            @Override
            public String toString(ClaimStatus status) {
                return status == null ? "" : status.getLabel();
            }

            @Override
            public ClaimStatus fromString(String text) {
                throw new UnsupportedOperationException("Selection only");
            }
        });

        Button applyButton = new Button("Cambiar estado del reclamo seleccionado");
        applyButton.setOnAction(e -> changeStatus());
        return new HBox(8, new Label("Nuevo estado:"), newStatus, applyButton);
    }

    private void changeStatus() {
        Claim selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UiSupport.showError("Selecciona un reclamo en la tabla.");
            return;
        }
        if (newStatus.getValue() == null) {
            UiSupport.showError("Elige el nuevo estado.");
            return;
        }
        UiSupport.run(() -> {
            claims.changeStatus(selected.getId(), newStatus.getValue());
            refresh();
        });
    }

    private void buildTable() {
        table.getColumns().add(UiSupport.<Claim>textColumn("ID reclamación", Claim::getId));
        table.getColumns().add(UiSupport.<Claim>textColumn("Garantía", c -> c.getWarranty().getId()));
        table.getColumns().add(UiSupport.<Claim>textColumn("Problema", Claim::getDescription));
        table.getColumns().add(UiSupport.<Claim>textColumn("Fecha", c -> c.getClaimDate().toString()));
        table.getColumns().add(statusColumn());
        table.setPlaceholder(new Label("No hay reclamaciones con este estado."));
    }

    /**
     *  Status column with a colored badge for each status.
     */
    private static TableColumn<Claim, ClaimStatus> statusColumn() {
        TableColumn<Claim, ClaimStatus> column = new TableColumn<>("Estado");
        column.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getStatus()));
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(ClaimStatus status, boolean empty) {
                super.updateItem(status, empty);
                getStyleClass().removeIf(c -> c.startsWith("badge-"));
                if (empty || status == null) {
                    setText(null);
                } else {
                    setText(status.getLabel());
                    getStyleClass().add("badge-" + status.name().toLowerCase().replace('_', '-'));
                }
            }
        });
        return column;
    }
}
