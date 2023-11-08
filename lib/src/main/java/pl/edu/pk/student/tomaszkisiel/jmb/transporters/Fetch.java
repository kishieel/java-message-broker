package pl.edu.pk.student.tomaszkisiel.jmb.transporters;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class Fetch implements Serializable {
    @Serial
    private static final long serialVersionUID = 40000L;
    private final String correlationId;

    private final String topic;

    public Fetch(String topic) {
        this.topic = topic;
        this.correlationId = UUID.randomUUID().toString();
    }

    public Fetch(String topic, String correlationId) {
        this.correlationId = correlationId;
        this.topic = topic;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getTopic() {
        return topic;
    }
}
