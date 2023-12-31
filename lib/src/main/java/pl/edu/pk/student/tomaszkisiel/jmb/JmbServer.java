package pl.edu.pk.student.tomaszkisiel.jmb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pk.student.tomaszkisiel.jmb.storage.InMemoryStorage;
import pl.edu.pk.student.tomaszkisiel.jmb.storage.JmbStorage;
import pl.edu.pk.student.tomaszkisiel.jmb.tools.SocketHandler;
import pl.edu.pk.student.tomaszkisiel.jmb.tools.TopicOrchestrator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JmbServer {

    private final Logger logger = LoggerFactory.getLogger(JmbServer.class);
    private final TopicOrchestrator orchestrator = new TopicOrchestrator();
    private Boolean running = true;
    private JmbStorage storage;
    private ServerSocket server;

    public void run(final int port) {
        this.run(port, 1000);
    }

    public void run(final int port, final int pool) {
        ExecutorService executor = Executors.newFixedThreadPool(pool);
        startServer(port);

        while (running) {
            try {
                Socket socket = server.accept();
                executor.submit(new SocketHandler(socket, orchestrator, getStorage()));
            } catch (IOException e) {
                running = false;
                throw new RuntimeException(e);
            }
        }

        stopServer();
        executor.shutdown();
    }

    public void terminate() {
        this.running = false;
        stopServer();
    }

    private JmbStorage getStorage() {
        return this.storage == null ? new InMemoryStorage(Duration.ofMinutes(15), 1000) : storage;
    }

    public JmbServer setStorage(JmbStorage storage) {
        this.storage = storage;
        return this;
    }

    private void startServer(final int port) {
        try {
            server = new ServerSocket(port);
            logger.info(String.format("Server listening on port %d...", port));
        } catch (IOException e) {
            logger.error(String.format("Server could not be started... %s", e));
            throw new RuntimeException(e);
        }
    }

    private void stopServer() {
        try {
            server.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
