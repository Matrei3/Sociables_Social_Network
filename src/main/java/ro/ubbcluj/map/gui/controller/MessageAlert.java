package ro.ubbcluj.map.gui.controller;

import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class MessageAlert {
    public static void showMessage(Stage owner, Alert.AlertType type, String header, String text){
        Alert message=new Alert(type);
        ImageView icon = new ImageView("ro/ubbcluj/map/gui/images/check.png");
        icon.setFitHeight(48);
        icon.setFitWidth(48);
        message.getDialogPane().setGraphic(icon);
        message.setHeaderText(header);
        message.setContentText(text);
        message.initOwner(owner);
        message.showAndWait();
    }

    public static void showErrorMessage(Stage owner, String text){
        Alert message=new Alert(Alert.AlertType.ERROR);
        message.initOwner(owner);
        message.setTitle("Error Message");
        message.setContentText(text);
        ImageView icon = new ImageView("ro/ubbcluj/map/gui/images/bonk.jpg");
        icon.setFitHeight(48);
        icon.setFitWidth(48);
        message.getDialogPane().setGraphic(icon);
        message.showAndWait();
    }
}

