package pl.edu.pk.student.tomaszkisiel.jmb.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pk.student.tomaszkisiel.jmb.dto.Subscribe;
import pl.edu.pk.student.tomaszkisiel.jmb.dto.Topic;
import pl.edu.pk.student.tomaszkisiel.jmb.dto.Unsubscribe;
import pl.edu.pk.student.tomaszkisiel.jmb.orchestrators.Orchestrator;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class ClientHandler extends Thread {
    private final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private final Orchestrator orchestrator;
    private final Map<Class<?>, Consumer<Object>> actions;

    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private Boolean running = true;

    public ClientHandler(Socket socket, Orchestrator orchestrator) throws IOException {
        this.orchestrator = orchestrator;
        this.actions = Map.of(
                Subscribe.class, (Object dto) -> this.onSubscribe((Subscribe) dto),
                Unsubscribe.class, (Object dto) -> this.onUnsubscribe((Unsubscribe) dto),
                Topic.class, (Object dto) -> this.onPublish((Topic<?>) dto)
        );
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        logger.info(String.format("Client connected (%s)", threadId()));

        try {
            while (running) {
                Object dto = in.readObject();
                actions.get(dto.getClass()).accept(dto);
            }
        } catch (IOException | ClassNotFoundException e) {
            orchestrator.unregister(this);
            running = false;

            if (e instanceof EOFException) logger.info(String.format("Client disconnected (%s)", threadId()));
            else throw new RuntimeException(e);
        }
    }

    private void onSubscribe(Subscribe dto) {
        orchestrator.register(dto.getTopic(), this);
    }

    private void onUnsubscribe(Unsubscribe dto) {
        orchestrator.unregister(dto.getTopic(), this);
    }

    private void onPublish(Topic<?> dto) {
        Set<ClientHandler> subscribers = orchestrator.getSubscribers(dto.getTopic());
        subscribers.forEach(subscriber -> {
            try {
                subscriber.out.writeObject(dto);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
