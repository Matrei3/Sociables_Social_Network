package ro.ubbcluj.map.gui.service;


import ro.ubbcluj.map.gui.domain.Friendship;
import ro.ubbcluj.map.gui.domain.Tuple;
import ro.ubbcluj.map.gui.domain.User;
import ro.ubbcluj.map.gui.domain.validators.UserException;
import ro.ubbcluj.map.gui.domain.validators.UserValidator;
import ro.ubbcluj.map.gui.repository.Repository;
import ro.ubbcluj.map.gui.repository.paging.Page;
import ro.ubbcluj.map.gui.repository.paging.Pageable;
import ro.ubbcluj.map.gui.repository.paging.PageableImplementation;
import ro.ubbcluj.map.gui.repository.paging.PagingRepository;
import ro.ubbcluj.map.gui.utils.events.ChangeEventType;
import ro.ubbcluj.map.gui.utils.events.UserChangeEvent;
import ro.ubbcluj.map.gui.utils.observer.Observable;
import ro.ubbcluj.map.gui.utils.observer.Observer;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UserService implements Observable<UserChangeEvent> {
    private final PagingRepository<Long,User,Long> repositoryUsers;
    private final List<Observer<UserChangeEvent>> observers=new ArrayList<>();
    private final UserValidator userValidator;
    private final Repository<Tuple<Long,Long>,Friendship<Long>> repositoryFriendships;

    public UserService(PagingRepository<Long,User,Long> repository, Repository<Tuple<Long,Long>,Friendship<Long>> repositoryFriendships, UserValidator userValidator) {
        this.repositoryUsers = repository;
        this.repositoryFriendships = repositoryFriendships;
        this.userValidator = userValidator;
    }

    public User addUserWithoutCredentials(String firstName, String LastName) {
        User newUser = new User(firstName, LastName);
        newUser.setId((long) (repositoryUsers.size() + 1));
        try {
            userValidator.validate(newUser);
        } catch (UserException exception) {
            return null;
        }

        if (repositoryUsers.save(newUser).isEmpty()) {
            return newUser;
        }
        notifyObservers(new UserChangeEvent(ChangeEventType.ADD,newUser));
        return null;
    }
    public User addUserWithCredentials(String firstName, String LastName,String username,String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        User newUser = new User(firstName, LastName,username,hash);

        newUser.setId((long) (repositoryUsers.size() + 1));
        try {
            userValidator.validate(newUser);
        } catch (UserException exception) {
            return null;
        }

        if (repositoryUsers.save(newUser).isEmpty()) {
            return newUser;
        }
        notifyObservers(new UserChangeEvent(ChangeEventType.ADD,newUser));
        return null;
    }
    public User removeUser(Long id) {
        Optional<User> entity = repositoryUsers.delete(id);
        if (entity.isEmpty())
            return null;
        else {
            Iterable<Friendship<Long>> allFriendships = repositoryFriendships.findAll();
            List<Friendship<Long>> result = new ArrayList<>();
            allFriendships.forEach(result::add);
            User user = entity.get();
            result.forEach(friendship -> {
                        if (Objects.equals(user.getId(), friendship.getID1())) {
                                Optional<User> optionalUser = repositoryUsers.findOne(friendship.getID2());
                                if(optionalUser.isPresent()) {
                                    User deletable2 = optionalUser.get();
                                    deletable2.removeFriend(user);
                                    Tuple<Long, Long> friendshipId = new Tuple<>(friendship.getID1(), friendship.getID2());
                                    repositoryFriendships.delete(friendshipId);
                                }

                        } else if (Objects.equals(user.getId(), friendship.getID2())) {
                            Optional<User> optionalUser = repositoryUsers.findOne(friendship.getID2());
                                if(optionalUser.isPresent()) {
                                    User deletable1 = optionalUser.get();
                                    deletable1.removeFriend(user);
                                    Tuple<Long, Long> friendshipId = new Tuple<>(friendship.getID1(), friendship.getID2());
                                    repositoryFriendships.delete(friendshipId);
                                }
                        }
                    }

            );
            notifyObservers(new UserChangeEvent(ChangeEventType.DELETE,entity.get()));
            return entity.get();
        }

    }

    public User modifyUser(Long id, String firstName, String lastName) {

        Optional<User> optional = repositoryUsers.findOne(id);
        if(optional.isPresent()) {
            User userToBeEdited = optional.get();
            User newUser = new User(firstName, lastName);
            newUser.setId(id);
            try {
                userValidator.validate(newUser);
            } catch (UserException exception) {
                System.out.println(exception.getMessage());
                return null;
            }
            newUser.setFriends(userToBeEdited.getFriends());
            repositoryUsers.update(newUser);
            notifyObservers(new UserChangeEvent(ChangeEventType.UPDATE, optional.get(), newUser));
            return newUser;
        }
        return null;
    }

    public Iterable<User> findAll() {
        return repositoryUsers.findAll();
    }
    public Page<User> findAllFriendsPaged(Long id,Pageable pageable){
        return repositoryUsers.findAllPaged(id,pageable);
    }
    public Page<User> findPage(Pageable pageable){
        return repositoryUsers.findAll(pageable);
    }
    public User findOne(Long id){
        Optional<User> entity = repositoryUsers.findOne(id);
        return entity.orElse(null);
    }

    public int size() {
        return repositoryUsers.size();
    }
    @Override
    public void addObserver(Observer<UserChangeEvent> eventObserver){
        observers.add(eventObserver);
    }

    @Override
    public void removeObserver(Observer<UserChangeEvent> e) {

    }

    @Override
    public void notifyObservers(UserChangeEvent event){
        observers.forEach(x->x.update(event));
    }
}
