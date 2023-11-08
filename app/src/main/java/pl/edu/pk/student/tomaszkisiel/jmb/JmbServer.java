package pl.edu.pk.student.tomaszkisiel.jmb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pk.student.tomaszkisiel.jmb.handlers.ClientHandler;
import pl.edu.pk.student.tomaszkisiel.jmb.hooks.GracefullyShutdown;
import pl.edu.pk.student.tomaszkisiel.jmb.orchestrators.Orchestrator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

public class JmbServer {
    private static final int PORT = 3000;
    private final Logger logger = LoggerFactory.getLogger(JmbServer.class);
    private final Orchestrator orchestrator = new Orchestrator();
    private Boolean running = true;


    public static void main(String[] args) {
        new JmbServer().run(PORT);
    }

    public void run(final int port) {
        Runtime.getRuntime().addShutdownHook(new GracefullyShutdown());
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
                new ClientHandler(socket, orchestrator).start();
            } catch (IOException e) {
                running = false;
                throw new RuntimeException(e);
            }
        }
    }
}
