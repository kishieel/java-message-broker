package pl.edu.pk.student.tomaszkisiel.jmb.storage;

import java.io.Serializable;
import java.util.List;

public interface JmbStorage {
    void put(String topic, Object payload);
    List<Object> getAll(String topic);
}
