package pl.edu.pk.student.tomaszkisiel.jmb.examples;

import pl.edu.pk.student.tomaszkisiel.jmb.JmbServer;
import pl.edu.pk.student.tomaszkisiel.jmb.storage.RedisStorage;

import java.time.Duration;

public class ServerExample {
    private static final int PORT = 3000;

    public static void main(String[] args) {
        new JmbServer().setStorage(new RedisStorage("redis://localhost:6379", Duration.ofMinutes(15), 3)).run(PORT);
    }
}
