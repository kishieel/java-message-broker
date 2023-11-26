package pl.edu.pk.student.tomaszkisiel.jmb.storage;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryStorage implements JmbStorage {
    private final Map<String, Cache<Long, Object>> storage = new HashMap<>();
    private final Duration ttl;
    private final Integer capacity;

    public InMemoryStorage(Duration ttl, Integer capacity) {
        this.ttl = ttl;
        this.capacity = capacity;
    }

    @Override
    public void put(String topic, Object payload) {
        Cache<Long, Object> cache = storage.getOrDefault(topic, createNewCache());
        cache.put(System.nanoTime(), payload);
        storage.put(topic, cache);
    }

    @Override
    public List<Object> getAll(String topic) {
        Cache<Long, Object> cache = storage.getOrDefault(topic, createNewCache());
        return cache.asMap().values().stream().toList();
    }

    private Cache<Long, Object> createNewCache() {
        return CacheBuilder.newBuilder().maximumSize(capacity).expireAfterWrite(ttl).build();
    }
}
