package pl.edu.pk.student.tomaszkisiel.jmb;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pk.student.tomaszkisiel.jmb.dto.Subscribe;
import pl.edu.pk.student.tomaszkisiel.jmb.dto.Topic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class JmbClient {
    private static final Logger logger = LoggerFactory.getLogger(JmbClient.class);
    private static final String HOST = "localhost";
    private static final Integer PORT = 3000;

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket(HOST, PORT);


        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        Worker worker = new Worker(in);
        worker.start();

        logger.info("Subscribing to 'ping' topic'");
        Subscribe dto1 = new Subscribe("ping");
        out.writeObject(dto1);

        Thread.sleep(5000);

        logger.info("Sending 'Hello world' to 'ping' topic'");
        Topic<String> dto2 = new Topic<>("ping", "Hello world");
        out.writeObject(dto2);

        Thread.sleep(10000);

        worker.interrupt();
        socket.close();
    }

    private static class Worker extends Thread {
        private final ObjectInputStream in;

        public Worker(ObjectInputStream in) {
            this.in = in;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Topic<String> dto = (Topic<String>) in.readObject();
                    logger.info(String.format("Received: %s %s", dto.getTopic(), dto.getPayload()));
                } catch (IOException | ClassNotFoundException e) {
                    logger.error(e.toString());
                }
            }
        }
    }
}
