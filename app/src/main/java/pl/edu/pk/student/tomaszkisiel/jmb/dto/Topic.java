package pl.edu.pk.student.tomaszkisiel.jmb.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class Topic<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 30000L;
    private final String correlationId;

    private final String topic;
    private final T payload;

    public Topic(String topic, T payload) {
        this.topic = topic;
        this.payload = payload;
        this.correlationId = UUID.randomUUID().toString();
    }

    public Topic(String topic, T payload, String correlationId) {
        this.correlationId = correlationId;
        this.payload = payload;
        this.topic = topic;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getTopic() {
        return topic;
    }

    public T getPayload() {
        return payload;
    }
}