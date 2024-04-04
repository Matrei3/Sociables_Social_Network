package ro.ubbcluj.map.gui.domain;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
public class User extends Entity<Long> {
    private String firstName;
    private String lastName;
    private String username;
    private byte[] password;

    public User(String firstName, String lastName, String username, byte[] password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
    }

    public User(String firstName, String lastName, String username) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    private List<User> friends = new ArrayList<>();

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<User> getFriends() {
        return friends;
    }
    private String printFriends(){
        StringBuilder friendList = new StringBuilder();
        friendList.append("[ ");
        for(User friend : friends)
            friendList.append(friend.getFirstName()).append(" ").append(friend.lastName).append(" ").append(friend.id).append(",");
        return friendList.substring(0,friendList.length()-1)+" ]";
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", friends=" + printFriends() +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User that)) return false;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }
    public void addFriend(User utilizator) {
        for(User friend : friends){
            if(Objects.equals(friend.id, utilizator.id))
                return;
        }
        friends.add(utilizator);

    }
    public void removeFriend(User utilizator){
        for(User friend : friends){
            if(Objects.equals(friend.id, utilizator.id)) {
                friends.remove(friend);
                return;
            }
        }

    }
    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getFriends());
    }
}