package ca.udem.maville.cli;

import java.io.IOException;
import java.util.Scanner;

import ca.udem.maville.api.client.MavilleRestClient;
import ca.udem.maville.enums.TravauxType; // This was an unnecessary import
import ca.udem.maville.model.Prestataire; // This was an unnecessary import
import ca.udem.maville.model.Resident;
import ca.udem.maville.model.User;

public class CLIMain {
    private final Scanner scanner = new Scanner(System.in);
    private User currentUser = null;
    private MavilleRestClient restClient;

    public CLIMain() {
        this.restClient = new MavilleRestClient("http://localhost:7070");
        run();
    }

    private void displayWelcomeScreen() {
        System.out.println("+-------------------------------------------------------------------------------------+");
        System.out.println("|.______    __            ___   .___________. _______   ______   .______    .___  ___.|");
        System.out.println(
                "||   _  \\  |  |          /   \\  |           ||   ____| /  __  \\  |   _  \\   |   \\/   ||");
        System.out.println("||  |_)  | |  |         /  ^  \\ `---|  |----`|  |__   |  |  |  | |  |_)  |  |  \\  /  ||");
        System.out.println("||   _  <  |  |        /  /_\\  \\    |  |     |   __|  |  |  |  | |   _  <   | |\\/| | ||");
        System.out.println("||  |_)  | |  `----.  /  _____  \\   |  |     |  |____ |  `--'  | |  |_)  |  | |  | | ||");
        System.out.println("||______/  |_______| /__/     \\__\\  |__|     |_______|  \\______/  |______/   |_|  |_| ||");
        System.out.println("+-------------------------------------------------------------------------------------+\n");
    }

    private void run() {
        displayWelcomeScreen();
        while (currentUser == null) {
            System.out.println("\n--- Authentication ---");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    login();
                    break;
                case "2":
                    register();
                    break;
                case "3":
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // In your CLIMain.java, update the login and register methods:

private void login() {
    System.out.print("Username: ");
    String username = scanner.nextLine();
    System.out.print("Password: ");
    String password = scanner.nextLine();

    try {
        if (restClient.login(username, password)) {
            currentUser = restClient.getCurrentUser();
            System.out.println("Login successful! Welcome " + currentUser.getUsername());
            displayUserMenu(currentUser);
        } else {
            System.out.println("Incorrect username or password.");
        }
    } catch (IOException e) {
        System.out.println("Error connecting to server: " + e.getMessage());
    }
}

private void register() {
    System.out.println("\n--- Registration ---");

    System.out.print("Full name: ");
    String name = scanner.nextLine();

    System.out.print("Username: ");
    String username = scanner.nextLine();
    
    System.out.print("Password: ");
    String password = scanner.nextLine();
    System.out.print("Email: ");
    String email = scanner.nextLine();
    System.out.print("Neighborhood: ");
    String neighbourhood = scanner.nextLine();
    
    System.out.println("Account type:");
    System.out.println("1. Resident");
    System.out.println("2. Service Provider");
    System.out.print("Choose your profile: ");
    String profileChoice = scanner.nextLine();
    
    User newUser = null;
    
    switch (profileChoice) {
        case "1":
            System.out.print("Phone: ");
            String phone = scanner.nextLine();
            newUser = new Resident(username, password, email, neighbourhood, name, phone);
            break;
            
        case "2":
            System.out.print("Company name: ");
            String nomEntreprise = scanner.nextLine();
            System.out.print("Company number: ");
            String numeroEntreprise = scanner.nextLine();
            newUser = new Prestataire(username, password, email, neighbourhood, 
                                     0, nomEntreprise, numeroEntreprise);
            break;
            
        default:
            System.out.println("Invalid option.");
            return;
    }

    try {
        if (restClient.register(newUser)) {
            System.out.println("Registration successful! Please login with your credentials.");
        } else {
            System.out.println("Registration failed. Username might already exist.");
        }
    } catch (IOException e) {
        System.out.println("Error connecting to server: " + e.getMessage());
    }
}

    private void displayUserMenu(User user) {
        if (user instanceof Resident) {
            ResidentMenu residentMenu = new ResidentMenu((Resident) user, scanner, restClient);
            residentMenu.displayMenu();
        } else if (user instanceof Prestataire) {
            // PrestataireMenu prestataireMenu = new PrestataireMenu((Prestataire) user, scanner, userRepository);
            // prestataireMenu.displayMenu();
        } else {
            System.out.println("Unknown user type.");
        }
    }

    public static TravauxType askForProblemType(Scanner scanner) {
        TravauxType[] types = TravauxType.values();

        for (int i = 0; i < types.length; i++) {
            System.out.println((i + 1) + ". " + types[i].name().replace('_', ' '));
        }

        System.out.print("Your choice (1-" + types.length + "): ");
        String choiceStr = scanner.nextLine();

        try {
            int choice = Integer.parseInt(choiceStr);
            if (choice < 1 || choice > types.length) {
                System.out.println("Invalid choice.");
                return null;
            }
            return types[choice - 1];
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return null;
        }
    }

    public static void validateField(String fieldName, String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Field '" + fieldName + "' cannot be empty.");
        }
    }
}
