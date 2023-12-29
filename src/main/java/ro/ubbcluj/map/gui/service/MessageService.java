package ro.ubbcluj.map.gui.service;

import ro.ubbcluj.map.gui.domain.Message;
import ro.ubbcluj.map.gui.domain.Tuple;
import ro.ubbcluj.map.gui.domain.User;
import ro.ubbcluj.map.gui.domain.validators.MessageException;
import ro.ubbcluj.map.gui.domain.validators.MessageValidator;
import ro.ubbcluj.map.gui.repository.Repository;
import ro.ubbcluj.map.gui.repository.paging.Page;
import ro.ubbcluj.map.gui.repository.paging.Pageable;
import ro.ubbcluj.map.gui.repository.paging.PagingRepository;
import ro.ubbcluj.map.gui.utils.events.ChangeEventType;
import ro.ubbcluj.map.gui.utils.events.MessageChangeEvent;
import ro.ubbcluj.map.gui.utils.observer.Observable;
import ro.ubbcluj.map.gui.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageService implements Observable<MessageChangeEvent> {

    private final PagingRepository<Long,Message,Tuple<Long,Long>> messageRepository;

    private final MessageValidator messageValidator;

    public MessageService(PagingRepository<Long, Message,Tuple<Long,Long>> messageRepository, MessageValidator messageValidator) {
        this.messageRepository = messageRepository;
        this.messageValidator = messageValidator;
    }

    public Message addMessage(Long idUser1, List<Long> idUser2, String text, LocalDateTime sentTime,Long reply){
        if(reply==null)
            reply = 0L;
        Message newMessage = new Message(idUser1,idUser2,text,sentTime,reply);
        try{
            messageValidator.validate(newMessage);
        } catch (MessageException e) {
            System.out.println(e.getMessage());
            return null;
        }
        Optional<Message> optional = messageRepository.save(newMessage);
        if(optional.isEmpty()){
            notifyObservers(new MessageChangeEvent(ChangeEventType.ADD,newMessage));
            return newMessage;
        }
        else{
            return null;
        }
    }
    public Iterable<Message> findAll(){
        return messageRepository.findAll();
    }
    public Page<Message> findAllMessagesForTwoUsers(Tuple<Long,Long> id_users, Pageable pageable){
        return messageRepository.findAllPaged(id_users,pageable);
    }
    private final List<Observer<MessageChangeEvent>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<MessageChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<MessageChangeEvent> e) {

    }

    @Override
    public void notifyObservers(MessageChangeEvent t) {
        observers.forEach(x->x.update(t));
    }
}
