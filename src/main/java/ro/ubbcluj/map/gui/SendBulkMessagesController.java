package ro.ubbcluj.map.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import ro.ubbcluj.map.gui.controller.MessageAlert;
import ro.ubbcluj.map.gui.service.MessageService;

import java.time.LocalDateTime;
import java.util.List;

public class SendBulkMessagesController {

    @FXML
    private TextField textFieldMessage;

    private List<Long> idsToSendMessagesTo;
    private Long senderId;

    private MessageService messageService;

    public void setRequirements(Long senderId, List<Long> idsToSendMessagesTo, MessageService messageService){
        this.idsToSendMessagesTo = idsToSendMessagesTo;
        this.senderId = senderId;
        this.messageService = messageService;
    }
    public void handleSend(ActionEvent actionEvent) {
        String text = textFieldMessage.getText();
        if(text.isEmpty()){
            MessageAlert.showErrorMessage(null,"Message must have a length");
            return;
        }
        idsToSendMessagesTo.forEach(id -> {
            messageService.addMessage(senderId,id,text,LocalDateTime.now(),null);
        });
    }
}
