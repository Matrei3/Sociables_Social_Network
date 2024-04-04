package ro.ubbcluj.map.gui.service;

import ro.ubbcluj.map.gui.domain.Friendship;
import ro.ubbcluj.map.gui.domain.Tuple;
import ro.ubbcluj.map.gui.domain.User;
import ro.ubbcluj.map.gui.domain.validators.FriendException;
import ro.ubbcluj.map.gui.domain.validators.FriendshipValidator;
import ro.ubbcluj.map.gui.repository.Repository;
import ro.ubbcluj.map.gui.utils.events.ChangeEventType;
import ro.ubbcluj.map.gui.utils.events.FriendshipChangeEvent;
import ro.ubbcluj.map.gui.utils.events.UserChangeEvent;
import ro.ubbcluj.map.gui.utils.observer.Observable;
import ro.ubbcluj.map.gui.utils.observer.Observer;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FriendshipService implements Observable<FriendshipChangeEvent> {
    private final Repository<Long, User> repositoryUsers;
    private final List<Observer<FriendshipChangeEvent>> observers = new ArrayList<>();
    private final Repository<Tuple<Long,Long>, Friendship<Long>> repositoryFriendships;
    private final FriendshipValidator friendshipValidator;

    public FriendshipService(Repository<Long,User> repositoryUsers, Repository<Tuple<Long,Long>,Friendship<Long>> repositoryFriendships, FriendshipValidator validator) {
        this.repositoryUsers = repositoryUsers;
        this.repositoryFriendships = repositoryFriendships;
        this.friendshipValidator = validator;
    }
    public Friendship<Long> addFriendship(Long idUser1, Long idUser2, LocalDateTime friendsFrom){
        if(repositoryUsers.findOne(idUser1).isEmpty() || repositoryUsers.findOne(idUser2).isEmpty()){
            return null;

        }
        User user1 =repositoryUsers.findOne(idUser1).get();
        User user2 =repositoryUsers.findOne(idUser2).get();
        Friendship<Long> friendship = new Friendship<>(idUser1,idUser2, friendsFrom);
        try{
            friendshipValidator.validate(friendship);
        } catch (FriendException exception) {
            System.out.println(exception.getMessage());
            return null;
        }
        Optional<Friendship<Long>> optional = repositoryFriendships.save(friendship);
        if(optional.isEmpty()) {
            user1.addFriend(user2);
            user2.addFriend(user1);
            notifyObservers(new FriendshipChangeEvent(ChangeEventType.ADD,friendship));
            return friendship;
        }
        else{
                Optional<Friendship<Long>> optionalFriendship = repositoryFriendships.update(friendship);
                if(optionalFriendship.isEmpty()) {
                    return null;
                }
                else {
                    notifyObservers(new FriendshipChangeEvent(ChangeEventType.UPDATE, friendship));
                    return friendship;
                }
        }
    }
    public String deleteFriendship(Long idUser1,Long idUser2){
        if(repositoryUsers.findOne(idUser1).isEmpty() || repositoryUsers.findOne(idUser2).isEmpty()){
            return "One or both users don't exist!";

        }
        Tuple<Long,Long> friendshipIdLeft = new Tuple<>(idUser1,idUser2);
        Tuple<Long,Long> friendshipIdRight = new Tuple<>(idUser2,idUser1);

        User user1= repositoryUsers.findOne(idUser1).get();
        User user2= repositoryUsers.findOne(idUser2).get();
        user1.removeFriend(user2);
        user2.removeFriend(user1);
        Optional<Friendship<Long>> deleted = repositoryFriendships.delete(friendshipIdLeft);
        if(deleted.isPresent()){
            repositoryFriendships.delete(friendshipIdRight);
            notifyObservers(new FriendshipChangeEvent(ChangeEventType.DELETE,deleted.get()));
            return "Friendship between user " + idUser1 + " and " + idUser2+ " deleted!";
        }
        else{
            return "Friendship between user " + idUser1 + " and " + idUser2+ " doesn't exists!";
        }
    }
    public Iterable<Friendship<Long>> findAll(){
        return repositoryFriendships.findAll();
    }
    public int size(){
        return repositoryFriendships.size();
    }
    public String friendsOfUserFromSpecificMonth(Long idUser,Integer month){
        Optional<User> user = repositoryUsers.findOne(idUser);
        final String[] result = {"Nume | Prenume | Data \n"};
        if(user.isPresent()){
            User actualUser = user.get();
            Iterable<Friendship<Long>> allFriendships =repositoryFriendships.findAll();
            List<Friendship<Long>> allFriendshipsList = new ArrayList<>();
            allFriendships.forEach(allFriendshipsList::add);
            allFriendshipsList.stream().filter(x -> Objects.equals(x.getID1(), actualUser.getId())).filter(x ->
                x.getDate().getMonth()== Month.of(month)
            ).forEach(x->{
                Optional<User> friend = repositoryUsers.findOne(x.getID2());
                User actualFriend;
                if(friend.isPresent()) {
                    actualFriend = friend.get();
                   result[0] = result[0] + actualFriend.getFirstName() + " | " + actualFriend.getLastName() + " | " + x.getDate() + '\n';
                }
            });
        }
        return result[0];
    }

    public Friendship<Long> modifyFriendship(Long id1, Long id2,LocalDateTime time) {
        /*Friendship<Long> newFriendship = new Friendship<>(id1,id2,time);
        Optional<Friendship<Long>> friendship = repositoryFriendships.update(newFriendship);
        if(friendship.isPresent()){
            if(status.equals("approved")) {
                repositoryUsers.findOne(newFriendship.getID1()).get().addFriend(repositoryUsers.findOne(newFriendship.getID2()).get());
                repositoryUsers.findOne(newFriendship.getID2()).get().addFriend(repositoryUsers.findOne(newFriendship.getID1()).get());
            }
            notifyObservers(new FriendshipChangeEvent(ChangeEventType.UPDATE,friendship.get(),newFriendship));
            return newFriendship;
        }*/
        return null;
    }

    @Override
    public void addObserver(Observer<FriendshipChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<FriendshipChangeEvent> e) {

    }

    @Override
    public void notifyObservers(FriendshipChangeEvent t) {
        observers.forEach(x->x.update(t));
    }
    //select f.id_user2,u.first_name,u.last_name,EXTRACT(MONTH from f.friends_from) as month from users u, friendships f group by f.id_user2,u.first_name,u.last_name,f.id_user1,u.id,f.friends_from having f.id_user2=u.id and f.id_user1=6
}
