package pl.edu.pk.student.tomaszkisiel.jmb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pk.student.tomaszkisiel.jmb.transporters.Subscribe;
import pl.edu.pk.student.tomaszkisiel.jmb.transporters.Topic;
import pl.edu.pk.student.tomaszkisiel.jmb.transporters.Unsubscribe;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.function.Consumer;

public class JmbClient extends Thread {
    private final Logger logger = LoggerFactory.getLogger(JmbClient.class);
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private Consumer<Topic<?>> consumer;
    private Boolean running = true;

    public JmbClient(String host, Integer port) throws IOException {
        this.socket = new Socket(host, port);
        this.in = new ObjectInputStream(socket.getInputStream());
        this.out = new ObjectOutputStream(socket.getOutputStream());
        logger.info("Client has started...");
    }

    @Override
    public void run() {
        while (running) {
            try {
                Object dto = in.readObject();
                if (dto instanceof Topic<?>) {
                    this.consumer.accept((Topic<?>) dto);
                    logger.info(String.format("Consumed '%s' topic", ((Topic<?>) dto).getTopic()));
                }
            } catch (IOException | ClassNotFoundException e) {
                if (e instanceof EOFException) {
                    logger.info("Disconnected");
                    running = false;
                } else {
                    logger.error(e.toString());
                    // @todo: handle exceptions
                }
            }
        }
    }

    public void subscribe(String topic) throws IOException {
        out.writeObject(new Subscribe(topic));
        logger.info(String.format("Subscribed to '%s' topic'", topic));
    }

    public void unsubscribe(String topic) throws IOException {
        out.writeObject(new Unsubscribe(topic));
        logger.info(String.format("Unsubscribed from '%s' topic'", topic));
    }

    public void publish(Topic<?> topic) throws IOException {
        out.writeObject(topic);
        logger.info(String.format("Published to '%s' topic'", topic.getTopic()));
    }

    public void consume(Consumer<Topic<?>> consumer) throws IOException {
        this.consumer = consumer;
    }
}
