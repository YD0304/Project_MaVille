package ca.udem.maville.cli;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import ca.udem.maville.api.client.MavilleRestClient;
import ca.udem.maville.enums.TravauxType;
import ca.udem.maville.model.Problem;
import ca.udem.maville.model.Resident;
import ca.udem.maville.model.Work;


public class ResidentMenu {
    private final Resident resident;
    private final Scanner scanner;
    private final MavilleRestClient mavilleRestClient;

    public ResidentMenu(Resident resident, Scanner scanner, MavilleRestClient mavilleRestClient) {
        this.resident = resident;
        this.scanner = scanner;
        this.mavilleRestClient = mavilleRestClient;
    }

    public void displayMenu() {
        while (true) {
            System.out.println("\n--- Menu Résident (Connecté: " + resident.getName() + ") ---");
            System.out.println("1. Consulter les travaux en cours");
            System.out.println("2. Consulter les travaux à venir dans les 3 prochains mois");
            System.out.println("3. Consulter les travaux par type");
            System.out.println("4. Signaler un problème");
            //System.out.println("5. Consulter mes notifications et abonnements");
            System.out.println("6. Retour au menu de sélection de profil");
            System.out.print("Choisissez une action: ");

            String choice = scanner.nextLine().trim().toLowerCase();
            switch (choice) {
                case "1" -> handleViewInProgressWorks();
                case "2" -> handleViewUpcomingWorks();
                case "3" -> handleViewFilterWorks();
                case "4" -> handleReportProblem();
                case "5" -> handleMyReportedProblems();
                //case "5" -> handleNotifications(residentConnecte);
                case "6" -> {
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void handleViewInProgressWorks() {
        try {
            List<Work> works = mavilleRestClient.getInProgressWorks();
            if (works.isEmpty()) {
                System.out.println("No work found for the selected criteria.");
            } else {
                System.out.println("Total of " + works.size() + " works retrieved:");
                System.out.println("---------------------------------");
                works.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println("Error while retrieving works: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleViewUpcomingWorks(){
        try {
            List<Work> works = mavilleRestClient.getUpcomingWorks();
            if (works.isEmpty()) {
                System.out.println("No work found for the selected criteria.");
            } else {
                System.out.println("Total of " + works.size() + " works retrieved:");
                System.out.println("---------------------------------");
                works.forEach(System.out::println);
            }
        } catch (IOException e) {
            System.out.println("Error while retrieving upcoming works: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleViewFilterWorks(){
        System.out.println("\n--- Filter Works ---");
        System.out.println("Filter by:\n1. Street\n2. Neighbourhood\n3. Work type");
        System.out.print("Your filter choice: ");
        String choice = scanner.nextLine().trim();
        String value = "";

        try {
            List<Work> works;
            if ("1".equals(choice)) {
                System.out.print("Enter the street name: ");
                value = scanner.nextLine().trim().toLowerCase();
                works = mavilleRestClient.getWorksByStreet(value);

            }
           else if ("2".equals(choice)) {
                System.out.print("Enter the neighbourhood name: ");
                value = scanner.nextLine().trim().toLowerCase();
                works = mavilleRestClient.getWorksByNeighbourhood(value);

            } else if ("3".equals(choice)) {
                TravauxType type = CLIMain.askForProblemType(scanner);
                if (type == null) {
                    System.out.println("No type selected. Operation cancelled.");
                    return;
                }
                value = type.name().replace('_', ' ');
                works = mavilleRestClient.getWorksByType(type.name());

            } else {
                System.out.println("Invalid choice. Displaying all works.");
                works = mavilleRestClient.getInProgressWorks();
            }

            if (works.isEmpty()) {
                String info = !value.isEmpty() ? " for \"" + value + "\"" : "";
                System.out.println("No work found" + info + ".");
            } else {
                System.out.println("\nTotal of " + works.size() + " works retrieved:");
                System.out.println("---------------------------------");
                works.forEach(t -> System.out.println(" - " + t));
            }

        } catch (Exception e) {
            System.out.println("\n[SYSTEM ERROR] " + e.getMessage());
        }
    }
    
    private void handleReportProblem() {
        System.out.println("\n--- Report a problem ---");

        System.out.print("Problem's neighborhood: ");
        String neighbourhood = scanner.nextLine().trim();
        CLIMain.validateField("neighbourhood", neighbourhood);

        System.out.print("Problem's street: ");
        String street = scanner.nextLine().trim();
        CLIMain.validateField("street", street);

        System.out.println("Problem type (choose a number from the following):");
        TravauxType type = CLIMain.askForProblemType(scanner);
        CLIMain.validateField("type", type != null ? type.name() : "");

        System.out.print("Problem's description: ");
        String description = scanner.nextLine().trim();
        CLIMain.validateField("description", description);

        System.out.print("Your contact information (email|phone): ");
        String contactInfo = scanner.nextLine().trim();
        CLIMain.validateField("contactInfo", contactInfo);

        try {
            Problem problem = new Problem(street, neighbourhood,type, description);
            mavilleRestClient.submitProblem(problem);
            System.out.println("✅ Problem reported successfully.");
        } catch (Exception e) {
            System.out.println("❌ Error while reporting the problem: " + e.getMessage());
        }
    }

    private void handleMyReportedProblems() {
        System.out.println("\n--- My Reported Problems ---");
        try {
            List<Problem> problems = mavilleRestClient.getMyReportedProblems(resident);
            if (problems.isEmpty()) {
                System.out.println("You have not reported any problems yet.");
            } else {
                System.out.println("Total of " + problems.size() + " problems reported:");
                System.out.println("---------------------------------");
                problems.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println("Error while retrieving your reported problems: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
    

    /**
    private void handleNotifications(Resident resident) {
        while (true) {
            System.out.println("\n--- Gérer les notifications et abonnements ---");
            System.out.println("1. Consulter mes abonnements");
            System.out.println("2. Consulter mes notifications");
            System.out.println("3. S'abonner à un type de notification");
            System.out.println("4. Se désabonner d'un type de notification");
            System.out.println("5. Retour au menu principal");
            System.out.print("Choisissez une action: ");

            String choix = scanner.nextLine().trim();
            try {
                switch (choix) {
                    case "1" -> consulterAbonnements(resident);
                    case "2" -> consulterNotifications(resident);
                    case "3" -> ajouterAbonnement(resident);
                    case "4" -> supprimerAbonnements(resident);
                    case "5" -> {
                        return;
                    }
                    default -> System.out.println("Option invalide.");
                }
            } catch (Exception e) {
                System.err.println("Erreur: " + e.getMessage());
            }
        }
    }

    private void consulterNotifications(Resident resident) {
        System.out.println("\n--- Consulter Notifications ---");
        System.out.println("1. Toutes les notifications");
        System.out.println("2. Notifications par type");
        System.out.print("Choisissez une option (1-2): ");

        String choix = scanner.nextLine().trim();

        try {
            List<Notification> notifications;

            if ("1".equals(choix)) {
                notifications = mavilleRestClient.consulterAllNotifications(resident.getId());
            } else if ("2".equals(choix)) {
                System.out.println("\nTypes de notification disponibles:");
                System.out.println("1. QUARTIER");
                System.out.println("2. RUE");
                System.out.print("Choisissez un type (1-2): ");

                String typeChoix = scanner.nextLine().trim();
                String type;

                if ("1".equals(typeChoix)) {
                    type = "QUARTIER";
                } else if ("2".equals(typeChoix)) {
                    type = "RUE";
                } else {
                    System.out.println("Choix invalide.");
                    return;
                }

                notifications = mavilleRestClient.consulterNotificationsByType(resident.getId(), type);
            } else {
                System.out.println("Choix invalide.");
                return;
            }

            if (notifications.isEmpty()) {
                System.out.println("Aucune notification trouvée.");
            } else {
                System.out.println("\nNotifications (" + notifications.size() + " trouvées):");
                System.out.println("---------------------------------");
                for (int i = 0; i < notifications.size(); i++) {
                    Notification notification = notifications.get(i);

                    // Chercher l’abonnement correspondant au type de notification
                    List<Abonnement> abonnements = mavilleRestClient.getAbonnementsResident(resident.getId());
                    Abonnement abo = abonnements.stream()
                            .filter(a -> a.getType() == notification.getAbonnementType())
                            // si tu as un champ "valeur"/"sujet", tu peux aussi le matcher ici
                            .findFirst()
                            .orElse(null);

                    String sujet = abo != null ? abo.getSujet() : "N/A";

                    System.out.printf("%d. [%s] %s (Sujet abonnement: %s)%n",
                            i + 1,
                            notification.getAbonnementType() != null ? notification.getAbonnementType() : "N/A",
                            notification.getMessage(),
                            sujet);
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des notifications : " + e.getMessage());
        }
    }

    private void consulterAbonnements(Resident resident) {
        try {
            List<Abonnement> abonnements = mavilleRestClient.getAbonnementsResident(resident.getId());

            if (abonnements.isEmpty()) {
                System.out.println("Aucun abonnement actif");
            } else {
                System.out.println("\n--- Vos abonnements actifs ---");
                System.out.println("+----+----------+----------------+----------+");
                System.out.println("| ID | Type     | Sujet          | Statut   |");
                System.out.println("+----+----------+----------------+----------+");

                for (Abonnement ab : abonnements) {
                    System.out.printf(
                            "| %-2d | %-8s | %-14s | %-8s |%n",
                            ab.getId(),
                            ab.getType(),
                            ab.getSujet(),
                            ab.isActive() ? "Actif" : "Inactif");
                }
                System.out.println("+----+----------+----------------+----------+");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des abonnements : " + e.getMessage());
        }
    }

    private void ajouterAbonnement(Resident resident) {
        try {
            System.out.println("\nTypes d'abonnement disponibles:");
            System.out.println("1. Abonnement à un quartier");
            System.out.println("2. Abonnement à une rue");
            System.out.print("Choisissez un type (1-2): ");

            String typeChoice = scanner.nextLine().trim();
            String type;
            String promptMessage;

            if ("1".equals(typeChoice)) {
                type = "QUARTIER";
                promptMessage = "Entrez le nom du quartier: ";

            } else if ("2".equals(typeChoice)) {
                type = "RUE";
                promptMessage = "Entrez le nom de la rue: ";
            } else {
                System.out.println("Choix invalide");
                return;
            }

            System.out.print(promptMessage);
            String valeur = scanner.nextLine().trim().toUpperCase();
            ;

            if (valeur.isEmpty()) {
                System.out.println("La valeur ne peut pas être vide.");
                return;
            }

            mavilleRestClient.ajouterAbonnementResident(resident.getId(), type, valeur);
            System.out.println("✅ Abonnement créé avec succès!");

        } catch (Exception e) {
            System.out.println("Erreur lors de la création de l'abonnement : " + e.getMessage());
        }
    }

    private void supprimerAbonnements(Resident resident) {
        try {
            List<Abonnement> abonnements = mavilleRestClient.getAbonnementsResident(resident.getId());

            if (abonnements.isEmpty()) {
                System.out.println("Aucun abonnement à supprimer");
                return;
            }

            // Improved table format with clear headers
            System.out.println("\n--- Vos abonnements actifs ---");
            System.out.println("+----+----------+----------------+----------+");
            System.out.println("| ID | Type     | Sujet          | Statut   |");
            System.out.println("+----+----------+----------------+----------+");

            for (Abonnement ab : abonnements) {
                System.out.printf(
                        "| %-2d | %-8s | %-14s | %-8s |%n",
                        ab.getId(),
                        ab.getType(),
                        ab.getSujet(),
                        ab.isActive() ? "Actif" : "Inactif");
            }
            System.out.println("+----+----------+----------------+----------+");

            // Clear instruction about what to enter
            System.out.print("\nEntrez l'ID de l'abonnement à supprimer (ou 'q' pour annuler) : ");
            String choixStr = scanner.nextLine().trim();

            // Handle quit option
            if ("q".equalsIgnoreCase(choixStr)) {
                System.out.println("Annulation de la suppression.");
                return;
            }

            try {
                int idToDelete = Integer.parseInt(choixStr);

                // Verify ID exists in the list
                boolean idExists = abonnements.stream()
                        .anyMatch(ab -> ab.getId() == idToDelete);

                if (!idExists) {
                    System.out.println("❌ Erreur : Aucun abonnement avec l'ID " + idToDelete);
                    return;
                }

                mavilleRestClient.supprimerAbonnement(idToDelete);
                System.out.println("✅ Abonnement ID " + idToDelete + " supprimé avec succès!");

            } catch (NumberFormatException e) {
                System.out.println("❌ Veuillez entrer un ID numérique valide ou 'q' pour annuler");
            }

        } catch (Exception e) {
            System.out.println("Erreur lors de la suppression de l'abonnement : " + e.getMessage());
        }
    }*/
