package ro.ubbcluj.map.gui.repository;

import ro.ubbcluj.map.gui.domain.Friendship;
import ro.ubbcluj.map.gui.domain.Tuple;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DataBaseRepositoryFriendships implements Repository<Tuple<Long,Long>, Friendship<Long>>{
    private final String url;
    private final String password;
    private final String username;

    public DataBaseRepositoryFriendships(String url, String username, String password) {
        this.url = url;
        this.password = password;
        this.username = username;
    }


    @Override
    public Optional<Friendship<Long>> findOne(Tuple<Long, Long> id) {
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("select * from friendships where (id_user1 = ? and id_user2=?) or (id_user1 = ? and id_user2=?)")
        ){
            statement.setLong(1,id.getLeft());
            statement.setLong(2,id.getRight());
            statement.setLong(3,id.getRight());
            statement.setLong(4,id.getLeft());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                Long id_user1 = resultSet.getLong("id_user1");
                Long id_user2 = resultSet.getLong("id_user2");
                LocalDateTime friendsFrom = resultSet.getTimestamp("friends_from").toLocalDateTime();
                Friendship<Long> u = new Friendship<>(id_user1,id_user2,friendsFrom);
                u.setId(id);
                return Optional.of(u);
            }
        }


        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Friendship<Long>> findAll() {
        Map<Tuple<Long,Long>,Friendship<Long>> userMap = new HashMap<>();
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("select * from friendships");
            ResultSet resultSet = statement.executeQuery()
        ){
            while(resultSet.next()){
                Long idUser1 = resultSet.getLong("id_user1");
                Long idUser2 = resultSet.getLong("id_user2");
                LocalDateTime friendsFrom = resultSet.getTimestamp("friends_from").toLocalDateTime();
                Tuple<Long,Long> idTuple = new Tuple<>(idUser1,idUser2);
                Friendship<Long> friendship = new Friendship<>(idUser1,idUser2,friendsFrom);
                userMap.put(idTuple,friendship);
        }
            return userMap.values();
    } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

        @Override
    public Optional<Friendship<Long>> save(Friendship<Long> entity) {
            try(Connection connection = DriverManager.getConnection(url,username,password);
                PreparedStatement statementFind = connection.prepareStatement("select * from friendships where (id_user1=? and id_user2=?) or (id_user1=? and id_user2=?)");
                PreparedStatement statementAdd = connection.prepareStatement("insert into friendships values(?,?,?)")
            ){
                statementFind.setLong(1,entity.getID1());
                statementFind.setLong(2,entity.getID2());
                statementFind.setLong(3,entity.getID2());
                statementFind.setLong(4,entity.getID1());
                ResultSet resultSet = statementFind.executeQuery();
                if(resultSet.next()){
                    return Optional.of(entity);
                }
                statementAdd.setLong(1,entity.getID1());
                statementAdd.setLong(2,entity.getID2());
                statementAdd.setDate(3,java.sql.Date.valueOf(entity.getDate().toLocalDate()));
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
    public Optional<Friendship<Long>> delete(Tuple<Long, Long> entity) {
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statementDelete = connection.prepareStatement("delete from friendships where (id_user1=? and id_user2=?) or (id_user1=? and id_user2=?)")
        ) {
            statementDelete.setLong(1,entity.getLeft());
            statementDelete.setLong(2,entity.getRight());
            statementDelete.setLong(3,entity.getRight());
            statementDelete.setLong(4,entity.getLeft());
            Friendship<Long> friendshipDeleted = new Friendship<>(-1L,-1L,LocalDateTime.now());
            if(statementDelete.executeUpdate()<1)
                return Optional.empty();
            {
                return Optional.of(friendshipDeleted);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship<Long>> update(Friendship<Long> entity) {
//        try(Connection connection = DriverManager.getConnection(url,username,password);
//            PreparedStatement statementUpdate = connection.prepareStatement("update friendships set status=? where (id_user1=? and id_user2=?) or (id_user1=? and id_user2=?)")
//        ) {
//            statementUpdate.setLong(2,entity.getID1());
//            statementUpdate.setLong(3,entity.getID2());
//            statementUpdate.setLong(4,entity.getID2());
//            statementUpdate.setLong(5,entity.getID1());
//            if(statementUpdate.executeUpdate()<1)
//                return Optional.empty();
//            {
//                return Optional.of(entity);
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
        return Optional.empty();
    }

    @Override
    public int size() {
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("select count(*) from friendships")
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
