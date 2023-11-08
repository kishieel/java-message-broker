package pl.edu.pk.student.tomaszkisiel.jmb.examples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pk.student.tomaszkisiel.jmb.JmbClient;
import pl.edu.pk.student.tomaszkisiel.jmb.transporters.Topic;

import java.io.IOException;
import java.util.Date;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class PojoExample {
    private static final Logger logger = LoggerFactory.getLogger(PojoExample.class);
    private static final String HOST = "localhost";
    private static final Integer PORT = 3000;

    public static void main(String[] args) throws Exception {
        JmbClient client = new JmbClient(HOST, PORT);

        client.subscribe("ntp:update");
        client.consume((Topic<?> topic) -> {
            if (topic.getTopic().equals("ntp:update")) {
                Date date = (Date) topic.getPayload();
                logger.info("Received: " + date.toString());
            }
        });

        client.start();

        Scanner scanner = new Scanner(System.in);
        String opt;

        while (true) {
            System.out.println("Specify application type: ");

            System.out.println("[1] Server");
            System.out.println("[2] Client");

            System.out.print("Opt: ");
            opt = scanner.nextLine();
            if (opt.equals("1")) {
                new Timer().scheduleAtFixedRate(new TimerTask(){
                    @Override
                    public void run(){
                        try {
                            client.publish(new Topic<>("ntp:update", new Date()));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },0,5000);
                break;
            } else if (opt.equals("2")) {
                break;
            }
        }
    }
}
