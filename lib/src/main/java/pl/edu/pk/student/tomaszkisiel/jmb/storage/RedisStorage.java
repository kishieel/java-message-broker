package pl.edu.pk.student.tomaszkisiel.jmb.storage;

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RDeque;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class RedisStorage implements JmbStorage {
    private final RedissonClient redis;
    private final Duration ttl;
    private final Integer capacity;

    public RedisStorage(String dsn, Duration ttl, Integer capacity) {
        Config config = new Config();
        config.useSingleServer().setAddress(dsn);
        this.redis = Redisson.create(config);
        this.ttl = ttl;
        this.capacity = capacity;
    }

    @Override
    public void put(String topic, Object payload) {
        RDeque<Object> backlog = redis.getDeque(topic);
        backlog.addLast(payload);

        if (backlog.size() > capacity) {
            backlog.removeFirst();
        }

        backlog.expire(ttl);
    }

    @Override
    public List<Object> getAll(String topic) {
        RDeque<Object> backlog = redis.getDeque(topic);
        return backlog.stream().toList();
    }
}
