package ro.ubbcluj.map.gui.repository.paging;

import ro.ubbcluj.map.gui.domain.PendingRequest;
import ro.ubbcluj.map.gui.domain.Tuple;
import ro.ubbcluj.map.gui.domain.User;
import ro.ubbcluj.map.gui.repository.DataBaseRepositoryPendingRequests;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class PendingRequestDBPagingRepository extends DataBaseRepositoryPendingRequests implements PagingRepository<Tuple<Long,Long>, PendingRequest> {
    public PendingRequestDBPagingRepository(String url, String username, String password) {
        super(url, username, password);
    }

    @Override
    public Page<PendingRequest> findAll(Pageable pageable) {
        Map<Tuple<Long,Long>, PendingRequest> pendingRequests = new HashMap<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from pending_requests limit ? offset ?");
             PreparedStatement statementUsername = connection.prepareStatement("select username from users where id=?")

        ) {
            statement.setInt(1,pageable.getPageSize());
            statement.setInt(2,(pageable.getPageNumber()-1) * pageable.getPageSize());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
            {
                Long id_user1= resultSet.getLong("id_user1");
                Long id_user2= resultSet.getLong("id_user2");
                String status=resultSet.getString("status");
                statementUsername.setLong(1,id_user1);
                Tuple<Long,Long> id = new Tuple<>(id_user1,id_user2);
                PendingRequest user=new PendingRequest(id_user1,id_user2,status);
                ResultSet resultSet1 = statementUsername.executeQuery();
                if(resultSet1.next()){
                    String username = resultSet1.getString("username");
                    user.setUsername(username);
                }
                user.setId(id);
                pendingRequests.put(id,user);

            }
            return new PageImplementation<>(pageable,pendingRequests.values().stream());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
