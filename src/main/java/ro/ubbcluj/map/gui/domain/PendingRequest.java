package ro.ubbcluj.map.gui.domain;

import java.time.LocalDateTime;

public class PendingRequest extends Entity<Tuple<Long,Long>>{

    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    private String status;
    private Long ID1;
    private Long ID2;

    @Override
    public String toString() {
        return "PendingRequest{" +
                ", status='" + status + '\'' +
                ", ID1=" + ID1 +
                ", ID2=" + ID2 +
                '}';
    }

    public PendingRequest(Long ID1, Long ID2,String status) {
        this.status = status;
        this.ID1 = ID1;
        this.ID2 = ID2;
    }

    public Long getID1() {
        return ID1;
    }

    public void setID1(Long ID1) {
        this.ID1 = ID1;
    }

    public Long getID2() {
        return ID2;
    }

    public void setID2(Long ID2) {
        this.ID2 = ID2;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
