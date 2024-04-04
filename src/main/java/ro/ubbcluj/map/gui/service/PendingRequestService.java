package ro.ubbcluj.map.gui.service;

import ro.ubbcluj.map.gui.domain.Friendship;
import ro.ubbcluj.map.gui.domain.PendingRequest;
import ro.ubbcluj.map.gui.domain.Tuple;
import ro.ubbcluj.map.gui.domain.User;
import ro.ubbcluj.map.gui.repository.Repository;
import ro.ubbcluj.map.gui.repository.paging.Page;
import ro.ubbcluj.map.gui.repository.paging.Pageable;
import ro.ubbcluj.map.gui.repository.paging.PageableImplementation;
import ro.ubbcluj.map.gui.repository.paging.PagingRepository;
import ro.ubbcluj.map.gui.utils.events.ChangeEventType;
import ro.ubbcluj.map.gui.utils.events.PendingRequestChangeEvent;
import ro.ubbcluj.map.gui.utils.observer.Observable;
import ro.ubbcluj.map.gui.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PendingRequestService implements Observable<PendingRequestChangeEvent> {
    private final Repository<Long, User> userRepository;
    private final List<Observer<PendingRequestChangeEvent>> observers = new ArrayList<>();
    private final Repository<Tuple<Long,Long>, Friendship<Long>> friendshipRepository;
    private final PagingRepository<Tuple<Long,Long>,PendingRequest,Long> pendingRequestRepository;

    public PendingRequestService(Repository<Long, User> userRepository, Repository<Tuple<Long, Long>, Friendship<Long>> friendshipRepository, PagingRepository<Tuple<Long, Long>, PendingRequest,Long> pendingRequestRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.pendingRequestRepository = pendingRequestRepository;
    }

    public PendingRequest addRequest(Long idUser1, Long idUser2, String status){
        if(userRepository.findOne(idUser1).isEmpty() || userRepository.findOne(idUser2).isEmpty()){
            return null;
        }
        User userWhoSentRequest = userRepository.findOne(idUser1).get();
        PendingRequest requestToBeAdded = new PendingRequest(idUser1,idUser2,status);
        requestToBeAdded.setUsername(userWhoSentRequest.getUsername());
        Optional<PendingRequest> optionalPendingRequest = pendingRequestRepository.save(requestToBeAdded);
        if(optionalPendingRequest.isEmpty()){
            notifyObservers(new PendingRequestChangeEvent(ChangeEventType.ADD,requestToBeAdded));
            return requestToBeAdded;
        }
        else{
            return null;
        }
    }
    public PendingRequest deleteRequest(Long idUser1,Long idUser2){
        if(userRepository.findOne(idUser1).isEmpty() || userRepository.findOne(idUser2).isEmpty()){
            return null;
        }
        Tuple<Long,Long> primaryKey = new Tuple<>(idUser1,idUser2);
        Optional<PendingRequest> deleted = pendingRequestRepository.delete(primaryKey);
        if(deleted.isEmpty()){
            return null;
        }
        else{
            PendingRequest pendingRequest = deleted.get();
            notifyObservers(new PendingRequestChangeEvent(ChangeEventType.DELETE,pendingRequest));
            return pendingRequest;
        }
    }
    public PendingRequest updateRequest(Long idUser1,Long idUser2,String newStatus){
        if(userRepository.findOne(idUser1).isEmpty() || userRepository.findOne(idUser2).isEmpty()){
            return null;
        }
        Tuple<Long,Long> primaryKey = new Tuple<>(idUser1,idUser2);
        Optional<PendingRequest> optional = pendingRequestRepository.findOne(primaryKey);
        if(optional.isPresent()){
            PendingRequest pendingRequest = optional.get();
            if(pendingRequest.getStatus().equals("pending") && newStatus.equals("approved")){
                Friendship<Long> newFriendship = new Friendship<>(idUser1,idUser2, LocalDateTime.now());
                User user1 = userRepository.findOne(idUser1).get();
                User user2 = userRepository.findOne(idUser2).get();
                user1.addFriend(user2);
                user2.addFriend(user1);
                friendshipRepository.save(newFriendship);
                pendingRequestRepository.delete(primaryKey);
                notifyObservers(new PendingRequestChangeEvent(ChangeEventType.UPDATE,pendingRequest));
                return pendingRequest;
            } else if (pendingRequest.getStatus().equals("pending") && newStatus.equals("rejected")) {
                pendingRequest.setStatus("rejected");
                pendingRequestRepository.update(pendingRequest);
                notifyObservers(new PendingRequestChangeEvent(ChangeEventType.UPDATE,pendingRequest));
                return pendingRequest;
            }
        }
        return null;
    }
    public Iterable<PendingRequest> findAll(){
        return pendingRequestRepository.findAll();
    }
    public Page<PendingRequest> findPage(Pageable pageable){
        return pendingRequestRepository.findAll(pageable);
    }
    public Page<PendingRequest> findPageForUser(Long id,Pageable pageable){
        return pendingRequestRepository.findAllPaged(id,pageable);
    }
    @Override
    public void addObserver(Observer<PendingRequestChangeEvent> e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer<PendingRequestChangeEvent> e) {

    }

    @Override
    public void notifyObservers(PendingRequestChangeEvent t) {
        observers.forEach(x -> x.update(t));
    }

    public int size() {
        return pendingRequestRepository.size();
    }
}
