package com.sigatec.ui;

import javafx.scene.control.Alert;

public class UiSupport {
    private UiSupport(){
    }
    public static void showError(String message){
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }
}
