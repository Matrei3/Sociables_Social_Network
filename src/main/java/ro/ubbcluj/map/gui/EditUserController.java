package ro.ubbcluj.map.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ro.ubbcluj.map.gui.controller.MessageAlert;
import ro.ubbcluj.map.gui.domain.User;
import ro.ubbcluj.map.gui.domain.validators.ValidationException;
import ro.ubbcluj.map.gui.service.UserService;

public class EditUserController {
    @FXML
    private TextField textFieldId;
    @FXML
    private TextField textFieldFirstName;
    @FXML
    private TextField textFieldLastName;
    @FXML
    private DatePicker datePickerDate;

    private UserService service;
    Stage dialogStage;
    User user;

    @FXML
    private void initialize() {
    }

    public void setService(UserService service,  Stage stage, User user) {
        this.service = service;
        this.dialogStage=stage;
        this.user=user;
        if (null != user) {
            setFields(user);
        }
        textFieldId.setEditable(false);
    }

    @FXML
    public void handleSave(){

        String firstName=textFieldFirstName.getText();
        String lastName=textFieldLastName.getText();
        User newUser=new User(firstName,lastName);

        if (null == this.user) {
            saveUser(newUser);
        }
        else {
            Long id = Long.valueOf(textFieldId.getText());
            newUser.setId(id);
            updateUser(newUser);
        }

    }
    @FXML

    private void updateUser(User user)
    {
        try {
            User updatedUser= this.service.modifyUser(user.getId(),user.getFirstName(),user.getLastName());
            if (updatedUser!=null) {
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Update user", "User was updated");
            }

        } catch (ValidationException e) {
            MessageAlert.showErrorMessage(null,e.getMessage());
        }
        dialogStage.close();
    }


    private void saveUser(User m)
    {
        // TODO
        try {
            User addUser= this.service.addUserWithoutCredentials(m.getFirstName(), m.getLastName());
            if (addUser==null)
                dialogStage.close();
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Saving user","User was saved");
        } catch (ValidationException e) {
            MessageAlert.showErrorMessage(null,e.getMessage());
        }
        dialogStage.close();

    }

    private void clearFields() {
        textFieldId.setText("");
        textFieldFirstName.setText("");
        textFieldLastName.setText("");
    }
    private void setFields(User user)
    {
        textFieldId.setText(user.getId().toString());
        textFieldFirstName.setText(user.getFirstName());
        textFieldLastName.setText(user.getLastName());
    }

    @FXML
    public void handleCancel(){
        dialogStage.close();
    }
}
