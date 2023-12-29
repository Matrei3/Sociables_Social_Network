package ro.ubbcluj.map.gui.repository.paging;


import ro.ubbcluj.map.gui.domain.User;
import ro.ubbcluj.map.gui.repository.DataBaseRepositoryUsers;

import java.sql.*;
import java.util.*;
import java.util.stream.Stream;

public class UserDBPagingRepository extends DataBaseRepositoryUsers implements PagingRepository<Long, User,Long>
{


    public UserDBPagingRepository(String url, String username, String password) {
        super(url, username, password);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {

        Map<Long,User> users = new HashMap<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from users limit ? offset ?");


        ) {
            statement.setInt(1,pageable.getPageSize() );
            statement.setInt(2,(pageable.getPageNumber()-1) * pageable.getPageSize());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
            {
                Long id= resultSet.getLong("id");
                String firstName=resultSet.getString("first_name");
                String lastName=resultSet.getString("last_name");
                String username=resultSet.getString("username");
                User user=new User(firstName,lastName,username);
                user.setId(id);
                users.put(id,user);

            }
            return new PageImplementation<>(pageable,users.values().stream());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<User> findAllPaged(Long id, Pageable pageable) {
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("select * from users where id = ?");
            PreparedStatement statementGetFriends = connection.prepareStatement("select u.id,u.first_name,u.last_name,u.username,f.friends_from from friendships f, users u group by u.id,u.first_name,u.last_name,f.id_user1,f.id_user2,f.friends_from,u.username having (u.id = f.id_user2 and f.id_user1 = ?) or (u.id = f.id_user1 and f.id_user2=?) limit ? offset ?")

        ){
            statement.setLong(1,id);
            statementGetFriends.setLong(1,id);
            statementGetFriends.setLong(2,id);
            statementGetFriends.setInt(3,pageable.getPageSize());
            statementGetFriends.setInt(4,(pageable.getPageNumber()-1) * pageable.getPageSize());
            ResultSet resultSet = statement.executeQuery();
            ResultSet resultFriends = statementGetFriends.executeQuery();
            if(resultSet.next()){
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String userName = resultSet.getString("username");
                User user = new User(firstName,lastName,userName);
                user.setId(id);
                List<User> friends = new ArrayList<>();
                while(resultFriends.next()){
                    Long idFriend = resultFriends.getLong("id");
                    String first_nameFriend = resultFriends.getString("first_name");
                    String last_nameFriend = resultFriends.getString("last_name");
                    String user_nameFriend = resultFriends.getString("username");
                    User friend = new User(first_nameFriend, last_nameFriend, user_nameFriend);
                    friends.add(friend);
                    friend.setId(idFriend);
                    user.addFriend(friend);
                }
                return new PageImplementation<>(pageable,friends.stream());
            }
        }


        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new PageImplementation<>(pageable, Stream.ofNullable(null));
    }
}
