package com.sigatec.ui;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.VBox;

import java.util.function.Function;

/** Shared helper methods for the views. */
public final class UiSupport {

    private UiSupport() {
    }

    /** Creates a table column that shows one text value per row. */
    public static <S> TableColumn<S, String> textColumn(String title, Function<S, String> value) {
        TableColumn<S, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cell -> new ReadOnlyStringWrapper(value.apply(cell.getValue())));
        return column;
    }

    /** Shows an error dialog with the given message. */
    public static void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }

    /** Shows an information dialog with the given message. */
    public static void showInfo(String message) {
        new Alert(Alert.AlertType.INFORMATION, message).showAndWait();
    }

    /** Asks the user to confirm an action. Returns true if they press OK. */
    public static boolean confirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK, ButtonType.CANCEL);
        return alert.showAndWait().filter(button -> button == ButtonType.OK).isPresent();
    }

    /** Runs a user action. Business and storage errors are shown in a dialog instead of closing the window. */
    public static void run(Runnable action) {
        try {
            action.run();
        } catch (RuntimeException ex) {
            showError(ex.getMessage() != null ? ex.getMessage() : ex.toString());
        }
    }

    /** Creates a bold section title. */
    public static Label sectionTitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("section-title");
        return label;
    }

    /** Creates a card: a white box with a border that groups related content. */
    public static VBox card(javafx.scene.Node... children) {
        VBox card = new VBox(12, children);
        card.getStyleClass().add("card");
        return card;
    }
}