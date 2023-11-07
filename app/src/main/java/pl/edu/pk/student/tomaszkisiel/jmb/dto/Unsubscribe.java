package pl.edu.pk.student.tomaszkisiel.jmb.dto;

import java.io.Serial;
import java.io.Serializable;

public class Unsubscribe implements Serializable {
    @Serial
    private static final long serialVersionUID = 20000L;

    private final String topic;

    public Unsubscribe(String topic) {
        this.topic = topic;
    }


    public String getTopic() {
        return topic;
    }
}