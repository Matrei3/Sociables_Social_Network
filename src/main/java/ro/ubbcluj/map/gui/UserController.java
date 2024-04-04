package ro.ubbcluj.map.gui;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ro.ubbcluj.map.gui.controller.MessageAlert;
import ro.ubbcluj.map.gui.domain.User;
import ro.ubbcluj.map.gui.repository.paging.PageableImplementation;
import ro.ubbcluj.map.gui.service.UserService;
import ro.ubbcluj.map.gui.utils.events.UserChangeEvent;
import ro.ubbcluj.map.gui.utils.observer.Observer;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserController implements Observer<UserChangeEvent> {

    UserService service;
    ObservableList<User> model = FXCollections.observableArrayList();


    @FXML
    TableView<User> tableView;
    @FXML
    TableColumn<User,Long> tableColumnId;
    @FXML
    TableColumn<User,String> tableColumnFirstName;
    @FXML
    TableColumn<User,String> tableColumnLastName;
    @FXML
    TableColumn<User,String> tableColumnUsername;
    @FXML
    TextField textFieldNumberOfItems;
    int pageNumber = 1;
    int pageSize = 4;

    public void setUserService(UserService userService) {
        service = userService;
        service.addObserver(this);
        initModel();
    }

    @FXML
    public void initialize() {
        tableColumnId.setCellValueFactory(new PropertyValueFactory<User, Long>("id"));
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        tableColumnUsername.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
        tableView.setItems(model);
        textFieldNumberOfItems.textProperty().addListener(o -> initModel());
    }

    private void initModel() {
        if(!Objects.equals(textFieldNumberOfItems.getText(), ""))
            pageSize = Integer.parseInt(textFieldNumberOfItems.getText());
        List<User> users = service.findPage(new PageableImplementation(pageNumber,pageSize)).getContent().collect(Collectors.toList());
        model.setAll(users);
    }


    public void handleDeleteUser(ActionEvent actionEvent) {
        User selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            User deleted = service.removeUser(selected.getId());
            if (null != deleted) {
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Delete", "User deleted successfully!");
            }
        } else MessageAlert.showErrorMessage(null, "No user selected!");
    }

    @Override
    public void update(UserChangeEvent userChangeEvent) {
        initModel();
    }

    @FXML
    public void handleUpdateUser(ActionEvent ev) {
        User selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            {
                showUserEditDialog(selected);
            }
        } else
            MessageAlert.showErrorMessage(null, "No user selected");
    }

    @FXML
    public void handleAddUser(ActionEvent ev) {

        showUserEditDialog(null);

    }

    public void showUserEditDialog(User user) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("views/useredit-view.fxml"));
            AnchorPane root = loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.getIcons().add(new Image("ro/ubbcluj/map/gui/images/build.png"));
            dialogStage.setTitle("Edit Tab");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            EditUserController editMessageViewController = loader.getController();
            editMessageViewController.setService(service, dialogStage, user);
            dialogStage.show();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    public void handleLastPage(ActionEvent actionEvent) {
        if(pageNumber!=1) {
            pageNumber--;
            initModel();
        }
    }

    public void handleNextPage(ActionEvent actionEvent) {
        if(pageNumber*pageSize<service.size()) {
            pageNumber++;
            initModel();
        }
    }
}


