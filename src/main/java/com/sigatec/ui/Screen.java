package com.sigatec.ui;

import javafx.scene.Parent;

public interface Screen {
    Parent build();

    void refresh();
}
