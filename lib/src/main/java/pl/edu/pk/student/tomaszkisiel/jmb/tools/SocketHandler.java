package pl.edu.pk.student.tomaszkisiel.jmb.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pk.student.tomaszkisiel.jmb.storage.JmbStorage;
import pl.edu.pk.student.tomaszkisiel.jmb.transporters.Fetch;
import pl.edu.pk.student.tomaszkisiel.jmb.transporters.Subscribe;
import pl.edu.pk.student.tomaszkisiel.jmb.transporters.Topic;
import pl.edu.pk.student.tomaszkisiel.jmb.transporters.Unsubscribe;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class SocketHandler implements Callable<Void> {
    private final Logger logger = LoggerFactory.getLogger(SocketHandler.class);
    private final TopicOrchestrator orchestrator;
    private final JmbStorage storage;
    private final Map<Class<?>, Consumer<Object>> actions;

    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private Boolean running = true;

    public SocketHandler(Socket socket, TopicOrchestrator orchestrator, JmbStorage storage) throws IOException {
        this.orchestrator = orchestrator;
        this.storage = storage;
        this.actions = Map.of(
                Subscribe.class, (Object dto) -> this.onSubscribe((Subscribe) dto),
                Unsubscribe.class, (Object dto) -> this.onUnsubscribe((Unsubscribe) dto),
                Topic.class, (Object dto) -> this.onPublish((Topic<?>) dto),
                Fetch.class, (Object dto) -> this.onFetch((Fetch) dto)
        );
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public Void call() throws Exception {
        logger.info(String.format("Client connected (%s)", Thread.currentThread().threadId()));

        try {
            while (running) {
                Object dto = in.readObject();
                actions.get(dto.getClass()).accept(dto);
            }
        } catch (IOException | ClassNotFoundException e) {
            orchestrator.unregister(this);
            running = false;

            if (e instanceof EOFException) logger.info(String.format("Client disconnected (%s)", Thread.currentThread().threadId()));
            else throw new RuntimeException(e);
        }

        return null;
    }

    private void onSubscribe(Subscribe dto) {
        orchestrator.register(dto.getTopic(), this);
    }

    private void onUnsubscribe(Unsubscribe dto) {
        orchestrator.unregister(dto.getTopic(), this);
    }

    private void onPublish(Topic<?> dto) {
        Set<SocketHandler> subscribers = orchestrator.getSubscribers(dto.getTopic());
        storage.put(dto.getTopic(), dto.getPayload());
        subscribers.forEach(subscriber -> {
            try {
                subscriber.out.writeObject(dto);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void onFetch(Fetch dto) {
        List<Object> payloads = storage.getAll(dto.getTopic());
        payloads.forEach(payload -> {
            try {
                Topic<?> topic = new Topic<>(dto.getTopic(), payload);
                out.writeObject(topic);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public long threadId() {
        return Thread.currentThread().threadId();
    }
}
