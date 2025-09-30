package ca.udem.maville;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import ca.udem.maville.cli.CLIMain;
import ca.udem.maville.repository.WorkRepository;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        boolean cliMode = false;
        int port = 7070;

        // Parse command line arguments
        for (int i = 0; i < args.length; i++) {
            if ("--port".equals(args[i]) && i + 1 < args.length) {
                try {
                    port = Integer.parseInt(args[i + 1]);
                    i++;
                } catch (NumberFormatException e) {
                    System.err.println("Invalid port: " + args[i + 1]);
                    return;
                }
            } else if ("--cli".equals(args[i])) {
                cliMode = true;
            }
        }

        if (cliMode) {
            // Run in CLI mode only
            new CLIMain();
        } else {
            // Run as Spring Boot web application
            System.setProperty("server.port", String.valueOf(port));
            ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);
            
            // Initialize repository with data
            WorkRepository repository = context.getBean(WorkRepository.class);
            repository.updateAndGetAllWork();
            
            System.out.println("Spring Boot application started on port " + port);
        }
    }
}