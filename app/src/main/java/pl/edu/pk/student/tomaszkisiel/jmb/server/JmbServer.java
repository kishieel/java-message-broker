package pl.edu.pk.student.tomaszkisiel.jmb.server;

import pl.edu.pk.student.tomaszkisiel.jmb.proto.SimpleDto;
import pl.edu.pk.student.tomaszkisiel.jmb.server.hooks.GracefullyShutdown;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

// exchange: name, type
// bindings: exchangeName, routingKey, queueName
// queue: name

public class JmbServer {
    private static final int PORT = 3000;

    public static void main(String[] args) throws Exception {
        new JmbServer().run(PORT);
    }

    public void run(final int port) throws Exception {
        Runtime.getRuntime().addShutdownHook(new GracefullyShutdown());

        ServerSocket server = new ServerSocket(port);
        System.out.printf("Server listening on port %d...%n", port);

        while (true) {
            Socket socket = server.accept();
            System.out.printf("Client connected (%s)%n", socket);

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            SimpleDto dto = (SimpleDto) in.readObject();
            System.out.printf("a:%s b:%d c:%f d:a:%b d:b:%b", dto.getA(), dto.getB(), dto.getC(), dto.getD().get("a"), dto.getD().get("b"));

            socket.close();
//            new Thread(() -> {
//                try {
//                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
//                    List<Integer> dto = (List<Integer>) in.readObject();
//                    System.out.println(dto);
////                    SimpleDto dto = (SimpleDto) in.readObject(); // .setObjectInputFilter()
////                    System.out.printf("a:%s b:%d c:%f", dto.getA(), dto.getB(), dto.getC());
//                } catch (Exception e) {
//                    System.out.println(e);
//                }
//            }).start();
        }
    }
}
