package ro.ubbcluj.map.gui.repository;

import ro.ubbcluj.map.gui.domain.Friendship;
import ro.ubbcluj.map.gui.domain.Message;
import ro.ubbcluj.map.gui.domain.Tuple;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DataBaseRepositoryMessages implements Repository<Long, Message>{

    private final String url;
    private final String username;
    private final String password;

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
                Long to = resultSet.getLong("to_user");
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
            PreparedStatement statementAdd = connection.prepareStatement("insert into messages(from_user,to_user,text,sent_time,reply) values(?,?,?,?,?)")
        ){
            statementAdd.setLong(1,entity.getFrom());
            statementAdd.setLong(2,entity.getTo());
            statementAdd.setString(3,entity.getText());
            statementAdd.setDate(4,java.sql.Date.valueOf(entity.getSentTime().toLocalDate()));
            if(entity.getReply()!=null)
                statementAdd.setLong(5,entity.getReply());
            else
                statementAdd.setNull(5, Types.NULL);
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
