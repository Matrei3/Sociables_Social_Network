package ro.ubbcluj.map.gui.repository.paging;

import ro.ubbcluj.map.gui.domain.Message;
import ro.ubbcluj.map.gui.domain.Tuple;
import ro.ubbcluj.map.gui.repository.DataBaseRepositoryMessages;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagesDBPagingRepository extends DataBaseRepositoryMessages implements PagingRepository<Long, Message,Tuple<Long,Long>> {
    public MessagesDBPagingRepository(String url, String username, String password) {
        super(url, username, password);
    }

    @Override
    public Page<Message> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Page<Message> findAllPaged(Tuple<Long,Long> id_user, Pageable pageable) {
        List<Message> messageMap = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("select m.id,m.text,m.from_user,t.to_user,m.sent_time,m.reply from messages m,to_messages t where m.id=t.id and ((m.from_user = ? and t.to_user= ?) or (m.from_user = ? and t.to_user= ?)) order by m.sent_time desc limit ?");

        ){
            statement.setLong(1,id_user.getLeft());
            statement.setLong(2,id_user.getRight());
            statement.setLong(3,id_user.getRight());
            statement.setLong(4,id_user.getLeft());
            statement.setInt(5,pageable.getPageSize());
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                Long id = resultSet.getLong("id");
                String text = resultSet.getString("text");
                Long id_from = resultSet.getLong("from_user");
                Long id_to = resultSet.getLong("to_user");
                List<Long> list = new ArrayList<>();
                list.add(id_to);
                LocalDateTime sentTime = resultSet.getTimestamp("sent_time").toLocalDateTime();
                Long replyId = resultSet.getLong("reply");
                Message message = new Message(id_from,list,text,sentTime,replyId);
                message.setId(id);
                messageMap.add(message);
            }
            return new PageImplementation<>(pageable,messageMap.stream());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
