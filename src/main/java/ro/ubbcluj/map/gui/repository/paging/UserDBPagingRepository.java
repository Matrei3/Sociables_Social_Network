package ro.ubbcluj.map.gui.repository.paging;


import ro.ubbcluj.map.gui.domain.User;
import ro.ubbcluj.map.gui.repository.DataBaseRepositoryUsers;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserDBPagingRepository extends DataBaseRepositoryUsers implements PagingRepository<Long, User>
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
}
