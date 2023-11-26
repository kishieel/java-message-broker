package pl.edu.pk.student.tomaszkisiel.jmb.examples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pk.student.tomaszkisiel.jmb.JmbClient;
import pl.edu.pk.student.tomaszkisiel.jmb.transporters.Topic;

import java.util.Scanner;

public class PrimitiveExample {
    private static final Logger logger = LoggerFactory.getLogger(PrimitiveExample.class);
    private static final String HOST = "localhost";
    private static final Integer PORT = 3000;

    public static void main(String[] args) throws Exception {
        JmbClient client = new JmbClient(HOST, PORT);

        client.subscribe("ping");
        client.consume((Topic<?> topic) -> {
            logger.info("Received: " + topic.getTopic() + " " + topic.getPayload());
        });

        client.start();

        Scanner scanner = new Scanner(System.in);
        String opt;

        while (true) {
            System.out.println("[1] Subscribe to topic");
            System.out.println("[2] Unsubscribe from topic");
            System.out.println("[3] Publish to topic");
            System.out.println("[4] Fetch topic");
            System.out.println("[5] Exit");

            System.out.print("Opt: ");
            opt = scanner.nextLine();
            if (opt.equals("1")) {
                System.out.print("Topic: ");
                opt = scanner.nextLine();
                client.subscribe(opt);
            } else if (opt.equals("2")) {
                System.out.print("Topic: ");
                opt = scanner.nextLine();
                client.unsubscribe(opt);
            } else if (opt.equals("3")) {
                System.out.print("Topic: ");
                String topic = scanner.nextLine();
                System.out.print("Payload: ");
                String payload = scanner.nextLine();
                client.publish(new Topic<>(topic, payload));
            } else if (opt.equals("4")) {
                System.out.print("Topic: ");
                opt = scanner.nextLine();
                client.fetch(opt);
            } else if (opt.equals("5")) {
                System.exit(0);
            }
        }
    }
}
