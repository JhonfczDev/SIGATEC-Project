package com.sigatec.ui;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DashboardView implements Screen {
    @Override
    public Parent build() {
        Label title = new Label ("Panel Principal");
        title.getStyleClass().add("page-title");

        VBox root = new VBox(20, title);
        root.setPadding(new Insets(28, 32, 28, 32));
        return root;
    }

    @Override
    public void refresh() {

    }
}
