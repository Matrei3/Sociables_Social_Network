package ro.ubbcluj.map.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ro.ubbcluj.map.gui.controller.MessageAlert;
import ro.ubbcluj.map.gui.domain.User;
import ro.ubbcluj.map.gui.service.FriendshipService;
import ro.ubbcluj.map.gui.service.MessageService;
import ro.ubbcluj.map.gui.service.PendingRequestService;
import ro.ubbcluj.map.gui.service.UserService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoginController {
    @FXML
    private TextField textFieldUsername;
    @FXML
    private TextField textFieldPassword;
    private UserService userService;
    private PendingRequestService pendingRequestService;
    private FriendshipService friendshipService;
    private MessageService messageService;

    @FXML
    private void initilize(){
    }
    public void setService(UserService userService, FriendshipService friendshipService , PendingRequestService pendingRequestService, MessageService messageService){
        this.userService = userService;
        this.pendingRequestService = pendingRequestService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        textFieldPassword.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    try {
                        handleLogin(null);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        textFieldUsername.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    textFieldPassword.requestFocus();
                }
            }
        });
    }

    public void handleRegister(ActionEvent actionEvent) {
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("views/register-view.fxml"));
            AnchorPane root = loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.getIcons().add(new Image("ro/ubbcluj/map/gui/images/build.png"));
            dialogStage.setTitle("Register Tab");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            RegisterController editMessageViewController = loader.getController();
            editMessageViewController.setUserService(userService, dialogStage);
            dialogStage.show();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void handleLogin(ActionEvent actionEvent) throws NoSuchAlgorithmException {
        String username = textFieldUsername.getText();
        String password = textFieldPassword.getText();
        if(username.equals("admin") && password.equals("parola")) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("views/user-view.fxml"));
                AnchorPane root = loader.load();

                // Create the dialog Stage.
                Stage dialogStage = new Stage();
                dialogStage.getIcons().add(new Image("/ro/ubbcluj/map/gui/images/build.png"));
                dialogStage.setTitle("Admin Tab");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);
                UserController editMessageViewController = loader.getController();
                editMessageViewController.setUserService(userService);
                dialogStage.show();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        else{
            Iterable<User> allUsers =  userService.findAll();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            String pass = new String(hash);
            byte[] hashed = pass.getBytes(StandardCharsets.UTF_8);
            String actualHashedPassword = new String(hashed);
            AtomicBoolean ok = new AtomicBoolean(false);
            allUsers.forEach(user -> {
                if(Objects.equals(user.getUsername(), textFieldUsername.getText()) && Objects.equals(actualHashedPassword,new String(user.getPassword())))
                {
                    try {
                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("/ro/ubbcluj/map/gui/views/homepage-view.fxml"));
                        AnchorPane root = loader.load();
                        Stage dialogStage = new Stage();
                        dialogStage.getIcons().add(new Image("/ro/ubbcluj/map/gui/images/networking.png"));
                        dialogStage.setTitle(user.getUsername());
                        dialogStage.initModality(Modality.WINDOW_MODAL);
                        Scene scene = new Scene(root);
                        dialogStage.setScene(scene);
                        HomePageController homePageController = loader.getController();
                        homePageController.setUserService(userService,pendingRequestService,messageService, user);
                        dialogStage.show();
                        ok.set(true);

                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
                else if(Objects.equals(user.getUsername(), textFieldUsername.getText())){
                    MessageAlert.showErrorMessage(null,"Incorrect password!");
                    ok.set(true);
                }

            });
            if(!ok.get())
                MessageAlert.showErrorMessage(null,"Username doesn't exist");
        }
    }
}
