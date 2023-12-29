package ro.ubbcluj.map.gui.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Long> {
    private Long from;
    private String usernameSender;
    public void setUsernameSender(String usernameSender) {
        this.usernameSender = usernameSender;
    }

    @Override
    public String toString() {
        if(reply!=0)
            return "(" + id + ") " +usernameSender + ": (reply to " + reply + ')' + text;
        return  "(" + id + ") " + usernameSender + ": " +
                text;
    }

    private List<Long> to;
    private String text;
    private LocalDateTime sentTime;
    private Long reply;
    public Message(Long from, List<Long> to, String text, LocalDateTime sentTime, Long reply) {

        this.from = from;
        this.to = to;
        this.sentTime = sentTime;
        this.text = text;
        this.reply = reply;
    }
    public Message(Long from, List<Long> to, String text, LocalDateTime sentTime) {

        this.from = from;
        this.to = to;
        this.sentTime = sentTime;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public List<Long> getTo() {
        return to;
    }

    public void setTo(List<Long> to) {
        this.to = to;
    }

    public LocalDateTime getSentTime() {
        return sentTime;
    }

    public void setSentTime(LocalDateTime sentTime) {
        this.sentTime = sentTime;
    }

    public Long getReply() {
        return reply;
    }

    public void setReply(Long reply) {
        this.reply = reply;
    }
}
