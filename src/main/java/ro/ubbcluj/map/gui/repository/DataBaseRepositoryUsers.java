package ro.ubbcluj.map.gui.repository;


import ro.ubbcluj.map.gui.domain.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DataBaseRepositoryUsers implements Repository<Long, User>{
    protected final String url;
    protected final String password;
    protected final String username;

    public DataBaseRepositoryUsers(String url, String username, String password) {
        this.url = url;
        this.password = password;
        this.username = username;
    }

    @Override
    public Optional<User> findOne(Long id) {

        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("select * from users where id = ?");
            PreparedStatement statementGetFriends = connection.prepareStatement("select u.id,u.first_name,u.last_name,u.username,f.friends_from from friendships f, users u group by u.id,u.first_name,u.last_name,f.id_user1,f.id_user2,f.friends_from,u.username having (u.id = f.id_user2 and f.id_user1 = ?) or (u.id = f.id_user1 and f.id_user2=?)")

        ){
            statement.setLong(1,id);
            statementGetFriends.setLong(1,id);
            statementGetFriends.setLong(2,id);
            ResultSet resultSet = statement.executeQuery();
            ResultSet resultFriends = statementGetFriends.executeQuery();
            if(resultSet.next()){
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String userName = resultSet.getString("username");
                User user = new User(firstName,lastName,userName);
                user.setId(id);
                while(resultFriends.next()){
                    Long idFriend = resultFriends.getLong("id");
                    String first_nameFriend = resultFriends.getString("first_name");
                    String last_nameFriend = resultFriends.getString("last_name");
                    String user_nameFriend = resultFriends.getString("username");
                    User friend = new User(first_nameFriend, last_nameFriend, user_nameFriend);
                    friend.setId(idFriend);
                    user.addFriend(friend);
                }
                return Optional.of(user);
            }
        }


         catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<User> findAll() {
        Map<Long,User> userMap = new HashMap<>();
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statementGetUsers = connection.prepareStatement("select id,first_name,last_name,username,password from users");
            PreparedStatement statementGetFriends = connection.prepareStatement("select u.id,u.first_name,u.last_name,u.username,f.friends_from from friendships f, users u group by u.id,u.first_name,u.last_name,f.id_user1,f.id_user2,f.friends_from having (u.id = f.id_user2 and f.id_user1 = ?) or (u.id = f.id_user1 and f.id_user2 = ?)")
            ){
            ResultSet resultSet = statementGetUsers.executeQuery();
            while(resultSet.next()){
                long id = resultSet.getLong("id");
                statementGetFriends.setLong(1,id);
                statementGetFriends.setLong(2,id);
                ResultSet resultFriends = statementGetFriends.executeQuery();

                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                byte[] hash = password.getBytes(StandardCharsets.UTF_8);
                User user = new User(firstName,lastName,username,hash);
                user.setId(id);
                while(resultFriends.next()){
                    Long idFriend = resultFriends.getLong("id");
                    String first_nameFriend = resultFriends.getString("first_name");
                    String last_nameFriend = resultFriends.getString("last_name");
                    String usernameFriend = resultFriends.getString("username");
                    User friend = new User(first_nameFriend, last_nameFriend, usernameFriend);
                    friend.setId(idFriend);
                    user.addFriend(friend);

                }
                userMap.put(id,user);
            }
            return userMap.values();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<User> save(User entity) {
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("insert into users(first_name,last_name,username,password) values(?,?,?,?)")
        ){
            statement.setString(1,entity.getFirstName());
            statement.setString(2,entity.getLastName());
            statement.setString(3,entity.getUsername());
            statement.setString(4,new String(entity.getPassword()));
            if(statement.executeUpdate()<1)
                return Optional.empty();
            else
                return Optional.of(entity);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> delete(Long aLong) {
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("delete from users where id=?")
        ){
            statement.setLong(1,aLong);
            User user = new User("Deleted","User");
           if(statement.executeUpdate()<1)
               return Optional.empty();
           else
               return Optional.of(user);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<User> update(User entity) {
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("update users set first_name=?,last_name=?,username=?,password=? where id=?")
        ){
            statement.setString(1,entity.getFirstName());
            statement.setString(2,entity.getLastName());
            statement.setString(3,entity.getUsername());
            statement.setString(4,new String(entity.getPassword()));
            statement.setLong(5,entity.getId());
            if(statement.executeUpdate()<1)
                return Optional.empty();
            else
                return Optional.of(entity);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int size() {
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("select count(*) from users");
            ResultSet resultSet = statement.executeQuery()
        ){
            if(resultSet.next()){
                return resultSet.getInt("count");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
}
