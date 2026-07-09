package ca.udem.maville.cli;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import ca.udem.maville.api.MavilleRestClient;
import ca.udem.maville.model.AbonnementType;
import ca.udem.maville.model.Notification;
import ca.udem.maville.model.Priorite;
import ca.udem.maville.model.Problem;
import ca.udem.maville.model.Project;
import ca.udem.maville.model.ProjectStatus;
import ca.udem.maville.model.Resident;
import ca.udem.maville.model.ResidentSubscription;
import ca.udem.maville.model.WorkType;

public class ResidentMenu {
    private final Resident resident;
    private final Scanner scanner;
    private final MavilleRestClient client;

    public ResidentMenu(Resident resident, Scanner scanner, MavilleRestClient client) {
        this.resident = resident;
        this.scanner = scanner;
        this.client = client;
    }

    public void displayMenu() {
        while (true) {
            System.out.println("\n--- Resident Menu (Logged in: " + resident.getNomComplet() + ") ---");
            System.out.println("1. View ongoing projects");
            System.out.println("2. View upcoming projects (next 3 months)");
            System.out.println("3. Filter projects by neighbourhood / street / type");
            System.out.println("4. Report a problem");
            System.out.println("5. View my reported problems");
            System.out.println("6. Manage subscriptions (notifications)");
            System.out.println("7. View my notifications");
            System.out.println("8. Logout");
            System.out.print("Choose an action: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> viewOngoingProjects();
                case "2" -> viewUpcomingProjects();
                case "3" -> filterProjects();
                case "4" -> reportProblem();
                case "5" -> viewMyReportedProblems();
                case "6" -> manageSubscriptions();
                case "7" -> viewNotifications();
                case "8" -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // ------------------------------------------------------------------------
    // 1. View ongoing projects (status = PROJECT_ONGOING)
    // ------------------------------------------------------------------------
    private void viewOngoingProjects() {
        System.out.println("\n--- Ongoing Projects ---");
        try {
            List<Project> projects = client.getProjectsByStatus(ProjectStatus.PROJECT_ONGOING);
            if (projects.isEmpty()) {
                System.out.println("No ongoing projects at the moment.");
            } else {
                projects.forEach(p -> System.out.printf("ID: %d | %s | %s | %s -> %s%n",
                        p.getId(), p.getTitle(), p.getStatus(),
                        p.getProposedStartDate(), p.getProposedEndDate()));
            }
        } catch (IOException e) {
            System.out.println("Error fetching projects: " + e.getMessage());
        }
        pressEnterToContinue();
    }

    // ------------------------------------------------------------------------
    // 2. View upcoming projects (proposedStartDate between today and +3 months)
    // ------------------------------------------------------------------------
    private void viewUpcomingProjects() {
        System.out.println("\n--- Upcoming Projects (next 3 months) ---");
        try {
            LocalDate today = LocalDate.now();
            LocalDate threeMonthsLater = today.plusMonths(3);
            List<Project> projects = client.getProjectsByDateRange(today, threeMonthsLater);
            if (projects.isEmpty()) {
                System.out.println("No upcoming projects in the next 3 months.");
            } else {
                projects.forEach(p -> System.out.printf("ID: %d | %s | Start: %s | End: %s%n",
                        p.getId(), p.getTitle(), p.getProposedStartDate(), p.getProposedEndDate()));
            }
        } catch (IOException e) {
            System.out.println("Error fetching upcoming projects: " + e.getMessage());
        }
        pressEnterToContinue();
    }

    // ------------------------------------------------------------------------
    // 3. Filter projects by neighbourhood, street, or work type
    // ------------------------------------------------------------------------
    private void filterProjects() {
        System.out.println("\n--- Filter Projects ---");
        System.out.println("1. By neighbourhood");
        System.out.println("2. By street");
        System.out.println("3. By work type (e.g., ROAD, WATER, etc.)");
        System.out.print("Your choice: ");
        String choice = scanner.nextLine().trim();

        try {
            List<Project> projects = null;
            switch (choice) {
                case "1" -> {
                    System.out.print("Enter neighbourhood: ");
                    String neighbourhood = scanner.nextLine().trim();
                    projects = client.getProjectsByNeighbourhood(neighbourhood);
                }
                case "2" -> {
                    System.out.print("Enter street: ");
                    String street = scanner.nextLine().trim();
                    projects = client.getProjectsByStreet(street);
                }
                case "3" -> {
                    WorkType type = askForProblemType();
                    if (type == null) return;
                    projects = client.getProjectsByType(type);
                }
                default -> {
                    System.out.println("Invalid choice.");
                    return;
                }
            }
            if (projects == null || projects.isEmpty()) {
                System.out.println("No projects match your filter.");
            } else {
                System.out.println("Found " + projects.size() + " project(s):");
                projects.forEach(p -> System.out.println(" - " + p.getTitle() + " (" + p.getProposedStartDate() + " -> " + p.getProposedEndDate() + ")"));
            }
        } catch (IOException e) {
            System.out.println("Error filtering projects: " + e.getMessage());
        }
        pressEnterToContinue();
    }

    // ------------------------------------------------------------------------
    // 4. Report a problem (unchanged logic, uses Problem entity)
    // ------------------------------------------------------------------------
    private void reportProblem() {
        System.out.println("\n--- Report a Problem ---");
        System.out.print("Neighbourhood: ");
        String neighbourhood = scanner.nextLine().trim();
        if (neighbourhood.isEmpty()) {
            System.out.println("Neighbourhood cannot be empty.");
            return;
        }
        System.out.print("Street: ");
        String street = scanner.nextLine().trim();
        if (street.isEmpty()) {
            System.out.println("Street cannot be empty.");
            return;
        }
        System.out.println("Problem type:");
        WorkType type = askForProblemType();
        if (type == null) return;
        System.out.print("Description: ");
        String description = scanner.nextLine().trim();
        if (description.isEmpty()) {
            System.out.println("Description cannot be empty.");
            return;
        }
        try {
            Problem problem = new Problem(street, neighbourhood, type, description, resident);
            problem.setPrioriteType(Priorite.NOT_ASSIGNED);
            Problem submitted = client.submitProblem(problem);
            System.out.println("✅ Problem reported successfully! ID: " + submitted.getId());
        } catch (IOException e) {
            System.out.println("❌ Error reporting problem: " + e.getMessage());
        }
        pressEnterToContinue();
    }

    private WorkType askForProblemType() {
        WorkType[] types = WorkType.values();
        for (int i = 0; i < types.length; i++) {
            System.out.println((i + 1) + ". " + types[i]);
        }
        System.out.print("Choose a type (number or name): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            System.out.println("No type selected.");
            return null;
        }
        try {
            int index = Integer.parseInt(input);
            if (index < 1 || index > types.length) {
                System.out.println("Invalid selection.");
                return null;
            }
            return types[index - 1];
        } catch (NumberFormatException e) {
            try {
                return WorkType.valueOf(input.toUpperCase());
            } catch (IllegalArgumentException ex) {
                System.out.println("Invalid work type.");
                return null;
            }
        }
    }

    // ------------------------------------------------------------------------
    // 5. View problems reported by this resident
    // ------------------------------------------------------------------------
    private void viewMyReportedProblems() {
        System.out.println("\n--- My Reported Problems ---");
        try {
            List<Problem> problems = client.getMyReportedProblems(resident.getId());
            if (problems.isEmpty()) {
                System.out.println("You haven't reported any problems yet.");
            } else {
                problems.forEach(p -> System.out.println("ID: " + p.getId() + " | " + p.getType() + " | " + p.getNeighbourhood() + " | " + p.getDescription()));
            }
        } catch (IOException e) {
            System.out.println("Error fetching problems: " + e.getMessage());
        }
        pressEnterToContinue();
    }

    // ------------------------------------------------------------------------
    // 6. Manage subscriptions (neighbourhood / street)
    // ------------------------------------------------------------------------
    private void manageSubscriptions() {
        while (true) {
            System.out.println("\n--- Subscription Management ---");
            System.out.println("1. View my subscriptions");
            System.out.println("2. Add a subscription (neighbourhood or street)");
            System.out.println("3. Deactivate a subscription");
            System.out.println("4. Back to main menu");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> viewSubscriptions();
                    case "2" -> addSubscription();
                    case "3" -> deactivateSubscription();
                    case "4" -> { return; }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void viewSubscriptions() throws IOException {
        List<ResidentSubscription> subs = client.getResidentSubscriptions(resident.getId());
        if (subs.isEmpty()) {
            System.out.println("No active subscriptions.");
        } else {
            System.out.println("ID | Type | Value | Active");
            for (ResidentSubscription s : subs) {
                System.out.printf("%d | %s | %s | %s%n",
                        s.getId(), s.getType(), s.isActive() ? "Yes" : "No");
            }
        }
    }

    private void addSubscription() throws IOException {
        System.out.println("Subscribe to:");
        System.out.println("1. Neighbourhood");
        System.out.println("2. Street");
        System.out.print("Choice: ");
        String typeChoice = scanner.nextLine().trim();
        AbonnementType type;
        String prompt;
        if ("1".equals(typeChoice)) {
            type = AbonnementType.QUARTIER;
            prompt = "Enter neighbourhood name: ";
        } else if ("2".equals(typeChoice)) {
            type = AbonnementType.RUE;
            prompt = "Enter street name: ";
        } else {
            System.out.println("Invalid choice.");
            return;
        }
        System.out.print(prompt);
        String value = scanner.nextLine().trim();
        if (value.isEmpty()) {
            System.out.println("Value cannot be empty.");
            return;
        }
        ResidentSubscription sub = client.subscribeResident(resident.getId(), type, value);
        System.out.println("✅ Subscribed (ID: " + sub.getId() + ")");
    }

    private void deactivateSubscription() throws IOException {
        viewSubscriptions();
        System.out.print("Enter subscription ID to deactivate: ");
        String idStr = scanner.nextLine().trim();
        if (idStr.isEmpty()) return;
        try {
            Long id = Long.parseLong(idStr);
            client.unsubscribeResident(id);
            System.out.println("✅ Subscription deactivated.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
    }

    // ------------------------------------------------------------------------
    // 7. View notifications (polling)
    // ------------------------------------------------------------------------
    private void viewNotifications() {
        System.out.println("\n--- My Notifications ---");
        try {
            List<Notification> notifs = client.getNotifications(resident.getEmail(), "RESIDENT");
            if (notifs.isEmpty()) {
                System.out.println("No notifications.");
            } else {
                for (Notification n : notifs) {
                    System.out.printf("[%s] %s %s%n",
                            n.getCreatedAt(), n.getMessage(),
                            n.isRead() ? "(read)" : "(unread)");
                }
                // Mark all as read (optional)
                System.out.print("Mark all as read? (y/n): ");
                if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                    for (Notification n : notifs) {
                        if (!n.isRead()) client.markNotificationRead(n.getId());
                    }
                    System.out.println("Notifications marked as read.");
                }
            }
        } catch (IOException e) {
            System.out.println("Error fetching notifications: " + e.getMessage());
        }
        pressEnterToContinue();
    }

    // ------------------------------------------------------------------------
    // Helper
    // ------------------------------------------------------------------------
    private void pressEnterToContinue() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}