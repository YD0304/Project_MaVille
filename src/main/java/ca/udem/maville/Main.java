package ca.udem.maville;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        int port = 7070;

        // Parse port argument if provided
        for (int i = 0; i < args.length; i++) {
            if ("--port".equals(args[i]) && i + 1 < args.length) {
                try {
                    port = Integer.parseInt(args[i + 1]);
                    i++;
                } catch (NumberFormatException e) {
                    System.err.println("Invalid port: " + args[i + 1]);
                    return;
                }
            }
        }

        System.setProperty("server.port", String.valueOf(port));
        SpringApplication.run(Main.class, args);
        System.out.println("Spring Boot application started on port " + port);
    }
}