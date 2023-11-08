package pl.edu.pk.student.tomaszkisiel.jmb.examples;

import pl.edu.pk.student.tomaszkisiel.jmb.JmbServer;

public class ServerExample {
    private static final int PORT = 3000;

    public static void main(String[] args) {
        new JmbServer().run(PORT);
    }
}
