package pl.edu.pk.student.tomaszkisiel.jmb.dto;

import java.io.Serial;
import java.io.Serializable;

public class Subscribe implements Serializable {
    @Serial
    private static final long serialVersionUID = 10000L;

    private final String topic;

    public Subscribe(String topic) {
        this.topic = topic;
    }


    public String getTopic() {
        return topic;
    }
}