package com.anas.code.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainFrameController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}