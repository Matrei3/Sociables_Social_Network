package ro.ubbcluj.map.gui.repository;

import ro.ubbcluj.map.gui.domain.Friendship;
import ro.ubbcluj.map.gui.domain.Message;
import ro.ubbcluj.map.gui.domain.Tuple;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.*;

public class DataBaseRepositoryMessages implements Repository<Long, Message>{

    protected final String url;
    protected final String username;
    protected final String password;

    public DataBaseRepositoryMessages(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<Message> findOne(Long id) {
        return Optional.empty();
    }

    @Override
    public Iterable<Message> findAll() {
        Map<Long,Message> messageMap = new HashMap<>();
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("select * from messages order by sent_time");
            ResultSet resultSet = statement.executeQuery()
        ){
            while(resultSet.next()){
                Long id = resultSet.getLong("id");
                Array to_array = resultSet.getArray("to_users");
                Object[] array = (Object[]) to_array.getArray();
                List<Long> to = new ArrayList<>();
                for (Object element : array) {
                    to.add((Long) element);
                }
                Long from = resultSet.getLong("from_user");
                LocalDateTime sentTime = resultSet.getTimestamp("sent_time").toLocalDateTime();
                Long replyId = resultSet.getLong("reply");
                String text = resultSet.getString("text");
                Message message = new Message(from,to,text,sentTime,replyId);
                message.setId(id);
                messageMap.put(id,message);
            }
            return messageMap.values();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<Message> save(Message entity) {
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statementAdd = connection.prepareStatement("insert into messages(from_user,to_users,text,sent_time,reply) values(?,?,?,?,?) returning id")
        ){
            statementAdd.setLong(1,entity.getFrom());
            Long[] long_list = entity.getTo().toArray(new Long[0]);
            Array array = connection.createArrayOf("BIGINT",long_list);
            statementAdd.setArray(2,array);
            statementAdd.setString(3,entity.getText());
            statementAdd.setTimestamp(4,java.sql.Timestamp.valueOf(entity.getSentTime()));
            if(entity.getReply()!=0)
                statementAdd.setLong(5,entity.getReply());
            else
                statementAdd.setNull(5, Types.NULL);
            ResultSet resultSet = statementAdd.executeQuery();
            if(resultSet.next()) {
                long id = resultSet.getLong("id");
                List<Long> to = entity.getTo();
                PreparedStatement statementInsert = connection.prepareStatement("insert into to_messages(id,to_user) values (?,?)");
                statementInsert.setLong(1,id);
                for(Long value : to){
                    statementInsert.setLong(2,value);
                    statementInsert.executeUpdate();
                }
                return Optional.empty();
            }
            else
                return Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> delete(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Optional<Message> update(Message entity) {
        return Optional.empty();
    }

    @Override
    public int size() {
        return 0;
    }
}
