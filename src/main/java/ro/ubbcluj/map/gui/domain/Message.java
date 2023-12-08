package ro.ubbcluj.map.gui.domain;

import java.time.LocalDateTime;

public class Message extends Entity<Long> {
    private Long from;
    private Long to;
    private String text;
    private LocalDateTime sentTime;
    private Long reply=null;
    public Message(Long from, Long to, String text, LocalDateTime sentTime, Long reply) {

        this.from = from;
        this.to = to;
        this.sentTime = sentTime;
        this.text = text;
        this.reply = reply;
    }
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public Long getTo() {
        return to;
    }

    public void setTo(Long to) {
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
