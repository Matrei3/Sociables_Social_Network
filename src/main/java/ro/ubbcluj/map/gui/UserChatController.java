package ro.ubbcluj.map.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import ro.ubbcluj.map.gui.domain.Message;
import ro.ubbcluj.map.gui.domain.User;
import ro.ubbcluj.map.gui.service.MessageService;
import ro.ubbcluj.map.gui.utils.events.MessageChangeEvent;
import ro.ubbcluj.map.gui.utils.events.PendingRequestChangeEvent;
import ro.ubbcluj.map.gui.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserChatController implements Observer<MessageChangeEvent> {
    @FXML
    private ListView<Message> listViewMessages = new ListView<>();
    ObservableList<Message> modelMessages = FXCollections.observableArrayList();
    @FXML
    private TextField textFieldMessage;
    private MessageService messageService;
    private User user;
    private User friend;
    public void handleSend(ActionEvent actionEvent) {
        if(listViewMessages.getSelectionModel().getSelectedItem()==null) {
            String text = textFieldMessage.getText();
            messageService.addMessage(user.getId(), friend.getId(), text, LocalDateTime.now(), null);
            textFieldMessage.clear();
        }
        else{
            Message selectedMessage = listViewMessages.getSelectionModel().getSelectedItem();
            String text = textFieldMessage.getText();
            messageService.addMessage(user.getId(), friend.getId(), text, LocalDateTime.now(), selectedMessage.getId());
            textFieldMessage.clear();
        }
        listViewMessages.getSelectionModel().clearSelection();
    }
    @FXML
    public void initialize(){

    }
    public void setRequirements(User user, User friend, MessageService messageService) {
        this.user = user;
        this.friend = friend;
        this.messageService = messageService;
        messageService.addObserver(this);
        initModel();
    }

    private void initModel() {
        Iterable<Message> messages = messageService.findAll();
        List<Message> filteredMessages = StreamSupport.stream(messages.spliterator(),false)
                .filter(message -> (message.getFrom().equals(user.getId()) && message.getTo().equals(friend.getId())) || message.getFrom().equals(friend.getId()) && message.getTo().equals(user.getId()))
                .toList();
        System.out.println(filteredMessages);
        for(Message message : filteredMessages){
                message.setUsernameSender(user.getId().equals(message.getFrom()) ? user.getUsername() : friend.getUsername());
        }
        modelMessages.setAll(filteredMessages);
        listViewMessages.setItems(modelMessages);
    }
    @Override
    public void update(MessageChangeEvent messageChangeEvent) {
        initModel();
    }

}
