package ro.ubbcluj.map.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ro.ubbcluj.map.gui.controller.MessageAlert;
import ro.ubbcluj.map.gui.domain.PendingRequest;
import ro.ubbcluj.map.gui.domain.Tuple;
import ro.ubbcluj.map.gui.domain.User;
import ro.ubbcluj.map.gui.repository.paging.PageableImplementation;
import ro.ubbcluj.map.gui.service.MessageService;
import ro.ubbcluj.map.gui.service.PendingRequestService;
import ro.ubbcluj.map.gui.service.UserService;
import ro.ubbcluj.map.gui.utils.events.PendingRequestChangeEvent;
import ro.ubbcluj.map.gui.utils.observer.Observer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class HomePageController implements Observer<PendingRequestChangeEvent> {
    ObservableList<User> modelFriends = FXCollections.observableArrayList();
    ObservableList<PendingRequest> modelRequests = FXCollections.observableArrayList();
    @FXML
    private TableView<User> tableViewFriends;
    @FXML
    private TableView<PendingRequest> tableViewRequests;
    @FXML
    private TextField friendUsernameTextField;
    @FXML
    private TextField textFieldNumberOfItems;
    @FXML
    private TextField textFieldNumberOfItems1;
    @FXML
    private TableColumn<User,String> tableColumnFirstName;
    @FXML
    private TableColumn<User,String> tableColumnLastName;
    @FXML
    private TableColumn<User,String> tableColumnUsername;
    @FXML
    private TableColumn<PendingRequest,String> tableColumnFriendshipStatus;
    @FXML
    private TableColumn<PendingRequest,String> tableColumnFriendshipUsername;
    private UserService userService;
    private PendingRequestService pendingRequestService;
    private MessageService messageService;
    private User user;
    private int pageNumberRequests = 1;
    private int pageSizeRequests = 4;
    private int pageNumberFriends = 1;
    private int pageSizeFriends = 4;
    public void setUserService(UserService userService, PendingRequestService pendingRequestService, MessageService messageService, User user) {
        this.userService = userService;
        this.user = user;
        this.pendingRequestService = pendingRequestService;
        this.messageService = messageService;
        pendingRequestService.addObserver(this);
        initModel();
    }
    @FXML
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        tableColumnUsername.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
        tableColumnFriendshipStatus.setCellValueFactory(new PropertyValueFactory<PendingRequest,String>("status"));
        tableColumnFriendshipUsername.setCellValueFactory(new PropertyValueFactory<PendingRequest,String>("username"));
        tableViewFriends.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableViewFriends.setItems(modelFriends);
        tableViewRequests.setItems(modelRequests);
        textFieldNumberOfItems.textProperty().addListener(o -> initModel());
        textFieldNumberOfItems1.textProperty().addListener(o -> initModel());

    }
    private void initModel() {
        if(!Objects.equals(textFieldNumberOfItems.getText(), ""))
            pageSizeRequests = Integer.parseInt(textFieldNumberOfItems.getText());
        if(!Objects.equals(textFieldNumberOfItems1.getText(), ""))
            pageSizeFriends = Integer.parseInt(textFieldNumberOfItems1.getText());
        user = userService.findOne(user.getId());
        List<User> userList = userService.findAllFriendsPaged(user.getId(),new PageableImplementation(pageNumberFriends,pageSizeFriends)).getContent().toList();
        List<PendingRequest> pendingRequests = pendingRequestService.findPageForUser(user.getId(),new PageableImplementation(pageNumberRequests, pageSizeRequests)).getContent().toList();
        if(!userList.isEmpty())
            modelFriends.setAll(userList);
        else {
            if(pageNumberFriends!=1)
                pageNumberFriends--;
        }
        if(!pendingRequests.isEmpty())
            modelRequests.setAll(pendingRequests);
        else {
            if(pageNumberRequests!=1)
                pageNumberRequests--;
        }

    }
    @Override
    public void update(PendingRequestChangeEvent userChangeEvent) {
        initModel();
    }

    public void handleAccept(ActionEvent actionEvent) {
        PendingRequest pendingRequest = tableViewRequests.getSelectionModel().getSelectedItem();
        if(pendingRequest != null){
            if(pendingRequest.getStatus().equals("rejected"))
                MessageAlert.showErrorMessage(null, "Request already rejected");
            else if(pendingRequest.getStatus().equals("pending")) {
                {
                    pendingRequestService.updateRequest(pendingRequest.getID1(), pendingRequest.getID2(), "approved");

                }
            }
        }
        else
            MessageAlert.showErrorMessage(null,"No request selected");
    }

    public void handleAdd(ActionEvent actionEvent) {
        String username = friendUsernameTextField.getText();
        List<User> userFriends = user.getFriends();
        AtomicBoolean ok = new AtomicBoolean(false);
        userFriends.forEach(friend ->{
            if(friend.getUsername().equals(username)) {
                MessageAlert.showErrorMessage(null, "You already befriended " + username);
                ok.set(true);
            }

        });
        if(ok.get())
            return;
        Iterable<User> userList = userService.findAll();
        userList.forEach(userIterable -> {
            if(userIterable.getUsername().equals(username)) {
                PendingRequest pendingRequest = pendingRequestService.addRequest(user.getId(), userIterable.getId(), "pending");
                if(pendingRequest!=null)
                    MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Friend Request","You're one step closer to making a friend");
                else
                    MessageAlert.showErrorMessage(null,"User rejected");
                ok.set(true);
            }
        });
        if(ok.get())
            return;
        MessageAlert.showErrorMessage(null, "Username doesn't exist");

    }

    public void handleReject(ActionEvent actionEvent) {
        PendingRequest pendingRequest = tableViewRequests.getSelectionModel().getSelectedItem();
        if(pendingRequest != null){
            if(pendingRequest.getStatus().equals("pending")) {
                {
                    pendingRequestService.updateRequest(pendingRequest.getID1(), pendingRequest.getID2(), "rejected");

                }
            }
            else
                MessageAlert.showErrorMessage(null,"Request already rejected");
        }
        else
            MessageAlert.showErrorMessage(null,"No request selected");
    }

    public void handleSendMessage(ActionEvent actionEvent) {

        var selectedCells = tableViewFriends.getSelectionModel().getSelectedCells();
        if(selectedCells.size()>1){
            List<Long> usersToSendTo = new ArrayList<>();
            for(TablePosition<User,?> pos : selectedCells){
                usersToSendTo.add(tableViewFriends.getItems().get(pos.getRow()).getId());
            }
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("views/send-bulk-messages-view.fxml"));
                AnchorPane root = loader.load();
                Stage dialogStage = new Stage();
                dialogStage.getIcons().add(new Image("ro/ubbcluj/map/gui/images/networking.png"));
                dialogStage.setTitle(user.getUsername() + " - send bulk messages");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);
                SendBulkMessagesController sendBulkMessagesController = loader.getController();
                sendBulkMessagesController.setRequirements(user.getId(),usersToSendTo,messageService);
                dialogStage.show();


            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        else{
            User friend = tableViewFriends.getSelectionModel().getSelectedItem();
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("views/user-chat-view.fxml"));
                AnchorPane root = loader.load();
                Stage dialogStage = new Stage();
                dialogStage.getIcons().add(new Image("ro/ubbcluj/map/gui/images/networking.png"));
                dialogStage.setTitle(user.getUsername() + "'s and " + friend.getUsername() + "'s chat");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);
                UserChatController userChatController = loader.getController();
                userChatController.setRequirements(user,friend,messageService);
                dialogStage.show();


            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void handleLastPageRequests(ActionEvent actionEvent) {
        if(pageNumberRequests !=1) {
            pageNumberRequests--;
            initModel();
        }
    }

    public void handleNextPageRequests(ActionEvent actionEvent) {
            pageNumberRequests++;
            initModel();
    }

    public void handleLastPageFriends(ActionEvent actionEvent) {
        if(pageNumberFriends !=1) {
            pageNumberFriends--;
            initModel();
        }
    }

    public void handleNextPageFriends(ActionEvent actionEvent) {
            pageNumberFriends++;
            initModel();
    }

}
