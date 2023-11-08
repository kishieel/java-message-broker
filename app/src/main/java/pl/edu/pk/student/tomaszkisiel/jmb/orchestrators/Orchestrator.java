package pl.edu.pk.student.tomaszkisiel.jmb.orchestrators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pk.student.tomaszkisiel.jmb.handlers.ClientHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Orchestrator {
    private final Logger logger = LoggerFactory.getLogger(Orchestrator.class);
    private final Map<String, Set<ClientHandler>> router;

    public Orchestrator() {
        router = new HashMap<>();
    }

    public void register(String topic, ClientHandler client) {
        Set<ClientHandler> clients = router.getOrDefault(topic, new HashSet<>());
        clients.add(client);
        router.put(topic, clients);
        logger.info(String.format("Client (%s) subscribed to '%s' topic", client.threadId(), topic));
    }

    public void unregister(String topic, ClientHandler client) {
        Set<ClientHandler> clients = router.getOrDefault(topic, new HashSet<>());
        clients.remove(client);
        router.put(topic, clients);
        logger.info(String.format("Client (%s) unsubscribed from '%s' topic", client.threadId(), topic));
    }

    public void unregister(ClientHandler client) {
        router.forEach((topic, clients) -> clients.remove(client));
    }

    public Set<ClientHandler> getSubscribers(String topic) {
        return router.getOrDefault(topic, new HashSet<>());
    }
}
