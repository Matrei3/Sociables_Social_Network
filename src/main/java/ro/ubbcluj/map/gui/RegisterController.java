package ro.ubbcluj.map.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ro.ubbcluj.map.gui.controller.MessageAlert;
import ro.ubbcluj.map.gui.domain.User;
import ro.ubbcluj.map.gui.service.UserService;

import java.security.NoSuchAlgorithmException;

public class RegisterController {
    @FXML
    private TextField textFieldFirstName;
    @FXML
    private TextField textFieldLastName;
    @FXML
    private TextField textFieldUsername;
    @FXML
    private TextField textFieldPassword;

    private UserService userService;
    private Stage dialogStage;
    @FXML
    private void initialize(){

    }
    public void setUserService(UserService userService, Stage stage){
        this.userService = userService;
        this.dialogStage = stage;
    }
    @FXML
    public void handleCancel(){
        dialogStage.close();
    }
    public void handleRegister(){
        String potentialErrors = "";
        String firstName = textFieldFirstName.getText();
        String lastName = textFieldLastName.getText();
        String username = textFieldUsername.getText();
        String password = textFieldPassword.getText();
        if(firstName.isEmpty())
            potentialErrors+="First name must have a length\n";
        if(lastName.isEmpty())
            potentialErrors+="Last name must have a length\n";
        if(username.isEmpty())
            potentialErrors+="Username must have a length\n";
        if(password.isEmpty())
            potentialErrors+="Password must have a length\n";
        if(!potentialErrors.isEmpty()) {
            MessageAlert.showErrorMessage(null, potentialErrors);
            return;
        }
        try{
            User addUser = this.userService.addUserWithCredentials(firstName,lastName,username,password);
            if(addUser==null)
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Saving user","User was saved");
            else
                MessageAlert.showErrorMessage(null, "Username already taken");
        } catch (Exception e) {
            MessageAlert.showErrorMessage(null,"Username already taken");
            return;
        }
        dialogStage.close();
    }
}
