package ro.ubbcluj.map.gui.repository;

import ro.ubbcluj.map.gui.domain.PendingRequest;
import ro.ubbcluj.map.gui.domain.Tuple;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DataBaseRepositoryPendingRequests implements Repository<Tuple<Long,Long>, PendingRequest> {
    protected final String url;
    protected final String password;
    protected final String username;

    public DataBaseRepositoryPendingRequests(String url, String username, String password) {
        this.url = url;
        this.password = password;
        this.username = username;
    }

    @Override
    public Optional<PendingRequest> findOne(Tuple<Long, Long> id) {
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("select * from pending_requests where id_user1=? and id_user2=? ")
        )
        {
            statement.setLong(1,id.getLeft());
            statement.setLong(2,id.getRight());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                Long id_user1 = resultSet.getLong("id_user1");
                Long id_user2 = resultSet.getLong("id_user2");
                String status = resultSet.getString("status");
                PendingRequest pendingRequest = new PendingRequest(id_user1,id_user2,status);
                return Optional.of(pendingRequest);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<PendingRequest> findAll() {
        Map<Tuple<Long,Long>, PendingRequest> pendingRequestsMap = new HashMap<>();
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("select * from pending_requests");
            PreparedStatement statementUsername = connection.prepareStatement("select username from users where id=?")
        ){
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                Long idUser1 = resultSet.getLong("id_user1");
                Long idUser2 = resultSet.getLong("id_user2");
                statementUsername.setLong(1,idUser1);
                ResultSet resultSet1 = statementUsername.executeQuery();

                String status = resultSet.getString("status");
                PendingRequest pendingRequest = new PendingRequest(idUser1,idUser2,status);
                if(resultSet1.next()){
                    String username = resultSet1.getString("username");
                    pendingRequest.setUsername(username);
                }
                Tuple<Long,Long> id = new Tuple<>(idUser1,idUser2);
                pendingRequestsMap.put(id,pendingRequest);
            }
            return pendingRequestsMap.values();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<PendingRequest> save(PendingRequest entity) {
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statementFind = connection.prepareStatement("select status from pending_requests where (id_user1=? and id_user2=?) or (id_user1=? and id_user2=?)");
            PreparedStatement statementAdd = connection.prepareStatement("insert into pending_requests values(?,?,?)")
        ){
            statementFind.setLong(1,entity.getID1());
            statementFind.setLong(2,entity.getID2());
            statementFind.setLong(3,entity.getID2());
            statementFind.setLong(4,entity.getID1());
            ResultSet resultSet = statementFind.executeQuery();
            if(resultSet.next()){
                String status = resultSet.getString("status");
                if(status.equals("rejected"))
                    return Optional.of(entity);
            }

            statementAdd.setLong(1,entity.getID1());
            statementAdd.setLong(2,entity.getID2());
            statementAdd.setString(3,entity.getStatus());
            if(statementAdd.executeUpdate()<1)
                return Optional.of(entity);
            {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<PendingRequest> delete(Tuple<Long, Long> entity) {
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statementDelete = connection.prepareStatement("delete from pending_requests where (id_user1=? and id_user2=?)")
        ) {
            statementDelete.setLong(1,entity.getLeft());
            statementDelete.setLong(2,entity.getRight());

            PendingRequest fakeRequest = new PendingRequest(-1L,-1L,"");
            if(statementDelete.executeUpdate()<1)
                return Optional.empty();
            {
                return Optional.of(fakeRequest);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<PendingRequest> update(PendingRequest entity) {
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statementUpdate = connection.prepareStatement("update pending_requests set status=? where (id_user1=? and id_user2=?)")
        ) {
            statementUpdate.setString(1,entity.getStatus());
            statementUpdate.setLong(2,entity.getID1());
            statementUpdate.setLong(3,entity.getID2());
            if(statementUpdate.executeUpdate()<1)
                return Optional.empty();
            {
                return Optional.of(entity);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public int size() {
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("select count(*) from pending_requests")
        ){
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next())
                return resultSet.getInt("count");
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
}
