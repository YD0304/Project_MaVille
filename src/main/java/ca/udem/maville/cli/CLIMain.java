package ca.udem.maville.cli;

import java.io.IOException;
import java.util.Scanner;

import ca.udem.maville.api.MavilleRestClient;
import ca.udem.maville.security.JwtAuthResponse;
import ca.udem.maville.model.Provider;
import ca.udem.maville.model.Resident;

public class CLIMain {
    private final Scanner scanner = new Scanner(System.in);
    private Object currentUser = null;      // can be Resident, Provider, or a special STPM flag
    private MavilleRestClient restClient;
    

    // STPM hardcoded credentials
    private static final String STPM_USERNAME = "stpm";
    private static final String STPM_PASSWORD = "admin123";

    public CLIMain() {
        this.restClient = new MavilleRestClient("http://localhost:7070");
        run();
    }

    private void displayWelcomeScreen() {
        System.out.println("+-------------------------------------------------------------------------------------+");
        System.out.println("|.______    __            ___   .___________. _______   ______   .______    .___  ___.|");
        System.out.println("||   _  \\  |  |          /   \\  |           ||   ____| /  __  \\  |   _  \\   |   \\/   ||");
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
            System.out.println("1. Resident Login");
            System.out.println("2. Service Provider Login");
            System.out.println("3. STPM Login (City Services)");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    residentLogin();
                    break;
                case "2":
                    providerLogin();
                    break;
                case "3":
                    stpmLogin();
                    break;
                case "4":
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    

    private void residentLogin() {
    System.out.print("Email: ");
    String email = scanner.nextLine();
    System.out.print("Password: ");
    String password = scanner.nextLine();  // read password securely (consider java.io.Console)
    try {
        JwtAuthResponse auth = restClient.login(email, password);
        if ("RESIDENT".equals(auth.getRole())) {
            Resident resident = restClient.getResidentByEmail(email);
            currentUser = resident;
            System.out.println("Welcome " + resident.getNomComplet());
            displayUserMenu();
        } else {
            System.out.println("Not a resident account");
        }
    } catch (IOException e) {
        System.out.println("Login failed: " + e.getMessage());
    }
}

    private void providerLogin() {
        System.out.print("Company Number: ");
        String companyNumber = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();  // read password securely (consider java.io.Console)
        try {
            JwtAuthResponse auth = restClient.login(companyNumber, password);
            if ("PROVIDER".equals(auth.getRole())) {
                Provider provider = restClient.getProviderByCompanyNumber(companyNumber);
                currentUser = provider;
                System.out.println("Welcome " + provider.getCompanyName());
                displayUserMenu();
            } else {
                System.out.println("Not a provider account");
            }
        } catch (IOException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private void stpmLogin() {
        System.out.println("\n--- STPM Login (City Services) ---");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (STPM_USERNAME.equals(username) && STPM_PASSWORD.equals(password)) {
            currentUser = "STPM";   // simple marker
            System.out.println("STPM login successful! Welcome City Services.");
            displayUserMenu();
        } else {
            System.out.println("Invalid STPM credentials.");
        }
    }

    private void displayUserMenu() {
        if (currentUser instanceof Resident) {
            ResidentMenu residentMenu = new ResidentMenu((Resident) currentUser, scanner, restClient);
            residentMenu.displayMenu();
        // } else if (currentUser instanceof Provider) {
        //     PrestataireMenu prestataireMenu = new PrestataireMenu(restClient, scanner);
        //     prestataireMenu.displayMenu((Provider) currentUser);
        // } else if ("STPM".equals(currentUser)) {
        //     STPMMenu stpmMenu = new STPMMenu(restClient, scanner);
        //     stpmMenu.displayMenu();
        } else {
            System.out.println("Unknown user type.");
        }
    }

    public static void main(String[] args) {
        new CLIMain();   // starts the CLI
    }
}