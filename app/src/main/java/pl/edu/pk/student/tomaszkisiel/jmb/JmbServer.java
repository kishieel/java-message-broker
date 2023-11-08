package pl.edu.pk.student.tomaszkisiel.jmb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pk.student.tomaszkisiel.jmb.tools.SocketHandler;
import pl.edu.pk.student.tomaszkisiel.jmb.tools.TopicOrchestrator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class JmbServer {
    private static final int PORT = 3000;
    private final Logger logger = LoggerFactory.getLogger(JmbServer.class);
    private final TopicOrchestrator orchestrator = new TopicOrchestrator();
    private Boolean running = true;


    public static void main(String[] args) {
        new JmbServer().run(PORT);
    }

    public void run(final int port) {
        ServerSocket server;

        try {
            server = new ServerSocket(port);
            logger.info(String.format("Server listening on port %d...", port));
        } catch (IOException e) {
            logger.error("Server could not be started...");
            throw new RuntimeException(e);
        }

        while (running) {
            try {
                Socket socket = server.accept();
                new SocketHandler(socket, orchestrator).start();
            } catch (IOException e) {
                running = false;
                throw new RuntimeException(e);
            }
        }
    }
}
