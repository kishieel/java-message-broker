package pl.edu.pk.student.tomaszkisiel.jmb.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TopicOrchestrator {
    private final Logger logger = LoggerFactory.getLogger(TopicOrchestrator.class);
    private final Map<String, Set<SocketHandler>> router;

    public TopicOrchestrator() {
        router = new HashMap<>();
    }

    public void register(String topic, SocketHandler client) {
        Set<SocketHandler> clients = router.getOrDefault(topic, new HashSet<>());
        clients.add(client);
        router.put(topic, clients);
        logger.info(String.format("Client (%s) subscribed to '%s' topic", client.threadId(), topic));
    }

    public void unregister(String topic, SocketHandler client) {
        Set<SocketHandler> clients = router.getOrDefault(topic, new HashSet<>());
        clients.remove(client);
        router.put(topic, clients);
        logger.info(String.format("Client (%s) unsubscribed from '%s' topic", client.threadId(), topic));
    }

    public void unregister(SocketHandler client) {
        router.forEach((topic, clients) -> clients.remove(client));
    }

    public Set<SocketHandler> getSubscribers(String topic) {
        return router.getOrDefault(topic, new HashSet<>());
    }
}
