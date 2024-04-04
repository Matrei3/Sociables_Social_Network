package ro.ubbcluj.map.gui;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import ro.ubbcluj.map.gui.domain.*;
import ro.ubbcluj.map.gui.domain.validators.FriendshipValidator;
import ro.ubbcluj.map.gui.domain.validators.MessageValidator;
import ro.ubbcluj.map.gui.domain.validators.UserValidator;
import ro.ubbcluj.map.gui.repository.*;
import ro.ubbcluj.map.gui.repository.paging.*;
import ro.ubbcluj.map.gui.service.FriendshipService;
import ro.ubbcluj.map.gui.service.MessageService;
import ro.ubbcluj.map.gui.service.PendingRequestService;
import ro.ubbcluj.map.gui.service.UserService;

import java.io.IOException;
import java.util.List;

public class StartApplication extends Application {

    PagingRepository<Long, User,Long> userRepository;
    Repository<Tuple<Long,Long>, Friendship<Long>> friendshipRepository;
    PagingRepository<Tuple<Long,Long>, PendingRequest,Long> pendingRequestRepository;
    PagingRepository<Long, Message,Tuple<Long,Long>> messageRepository;
    UserService userService;
    FriendshipService friendshipService;
    PendingRequestService pendingRequestService;
    MessageService messageService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parameters params = getParameters();
        List<String> list = params.getRaw();
        String url= list.get(0);
        String username = list.get(1);
        String password = list.get(2);


        userRepository = new UserDBPagingRepository(url,username,password);
        friendshipRepository = new DataBaseRepositoryFriendships(url,username,password);
        pendingRequestRepository = new PendingRequestDBPagingRepository(url,username,password);
        messageRepository = new MessagesDBPagingRepository(url,username,password);
        UserValidator userValidator = new UserValidator();
        MessageValidator messageValidator = new MessageValidator();
        FriendshipValidator friendshipValidator = new FriendshipValidator();
        userService = new UserService(userRepository,friendshipRepository,userValidator);
        friendshipService = new FriendshipService(userRepository,friendshipRepository,friendshipValidator);
        pendingRequestService = new PendingRequestService(userRepository,friendshipRepository,pendingRequestRepository);
        messageService = new MessageService(messageRepository,messageValidator);
        initView(primaryStage);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Sociables");
        primaryStage.getIcons().add(new Image("ro/ubbcluj/map/gui/images/networking.png"));
        primaryStage.show();


    }

    private void initView(Stage primaryStage) throws IOException {

        FXMLLoader userLoader = new FXMLLoader();
        userLoader.setLocation(getClass().getResource("views/login-view.fxml"));
        AnchorPane userLayout = userLoader.load();
        primaryStage.setScene(new Scene(userLayout));
        LoginController userService = userLoader.getController();
        userService.setService(this.userService,this.friendshipService,this.pendingRequestService,this.messageService);

    }
}
