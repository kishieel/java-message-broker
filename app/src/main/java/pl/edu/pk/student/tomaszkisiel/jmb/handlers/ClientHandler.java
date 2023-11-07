package pl.edu.pk.student.tomaszkisiel.jmb.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pk.student.tomaszkisiel.jmb.dto.Subscribe;
import pl.edu.pk.student.tomaszkisiel.jmb.dto.Topic;
import pl.edu.pk.student.tomaszkisiel.jmb.dto.Unsubscribe;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.function.Consumer;

public class ClientHandler extends Thread {
    private final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private final String uuid;
    private final Socket socket;
    private final Map<Class<?>, Consumer<Object>> actions;

    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public ClientHandler(String uuid, Socket socket) throws IOException {
        this.uuid = uuid;
        this.socket = socket;
        this.actions = Map.of(
                Subscribe.class, (Object dto) -> this.subscribe((Subscribe) dto),
                Unsubscribe.class, (Object dto) -> this.unsubscribe((Unsubscribe) dto),
                Topic.class, (Object dto) -> this.consume((Topic<?>) dto)
        );
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        logger.info(String.format("Client connected (%s)", this.uuid));

        try {
            while (true) {
                Object dto = in.readObject();
                actions.get(dto.getClass()).accept(dto);
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.error(String.format("Client disconnected (%s)", uuid));
        }
    }

    private void subscribe(Subscribe dto) {
        logger.info(String.format("Client (%s) subscribed to '%s' topic", uuid, dto.getTopic()));
    }

    private void unsubscribe(Unsubscribe dto) {
        logger.info(String.format("Client (%s) unsubscribed from '%s' topic", uuid, dto.getTopic()));
    }

    private void consume(Topic<?> dto) {
        try {
            logger.info(String.format("topic:%s payload:%s", dto.getTopic(), dto.getPayload()));
            out.writeObject(dto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
