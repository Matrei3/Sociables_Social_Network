package ro.ubbcluj.map.gui.domain;

import java.time.LocalDateTime;


public class Friendship<ID> extends Entity<Tuple<ID,ID>> {

    private final LocalDateTime date;
    private final ID ID1,ID2;

    public Friendship(ID ID1, ID ID2,LocalDateTime date) {
        this.date = date;
        this.ID1 = ID1;
        this.ID2 = ID2;
    }

    @Override
    public String toString() {
        return "Friendship{" +
                "date=" + date +
                ", ID1=" + ID1 +
                ", ID2=" + ID2 +
                '}';
    }

    public ID getID1() {
        return ID1;
    }

    public ID getID2() {
        return ID2;
    }
    public LocalDateTime getDate() {
        return date;
    }

}
