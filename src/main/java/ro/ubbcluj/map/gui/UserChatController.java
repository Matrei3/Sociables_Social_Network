package ro.ubbcluj.map.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import ro.ubbcluj.map.gui.domain.Message;
import ro.ubbcluj.map.gui.domain.Tuple;
import ro.ubbcluj.map.gui.domain.User;
import ro.ubbcluj.map.gui.repository.paging.PageableImplementation;
import ro.ubbcluj.map.gui.service.MessageService;
import ro.ubbcluj.map.gui.utils.events.MessageChangeEvent;
import ro.ubbcluj.map.gui.utils.events.PendingRequestChangeEvent;
import ro.ubbcluj.map.gui.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.*;
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
    private int pageSize = 5;
    public void handleSend(ActionEvent actionEvent) {
        if(listViewMessages.getSelectionModel().getSelectedItem()==null) {
            String text = textFieldMessage.getText();
            pageSize++;
            messageService.addMessage(user.getId(), List.of(friend.getId()), text, LocalDateTime.now(), null);

            textFieldMessage.clear();
        }
        else{
            Message selectedMessage = listViewMessages.getSelectionModel().getSelectedItem();
            String text = textFieldMessage.getText();
            pageSize++;
            messageService.addMessage(user.getId(), List.of(friend.getId()), text, LocalDateTime.now(), selectedMessage.getId());

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
        List<Message> messages = new ArrayList<>(messageService.findAllMessagesForTwoUsers(new Tuple<>(user.getId(), friend.getId()), new PageableImplementation(1, pageSize)).getContent().toList());
        Collections.reverse(messages);
        for(Message message : messages){
            if(Objects.equals(message.getFrom(), friend.getId()))
                message.setUsernameSender(friend.getUsername());
            else
                message.setUsernameSender(user.getUsername());
        }
        modelMessages.setAll(messages);
        listViewMessages.setItems(modelMessages);
    }
    @Override
    public void update(MessageChangeEvent messageChangeEvent) {
        initModel();
    }

}
