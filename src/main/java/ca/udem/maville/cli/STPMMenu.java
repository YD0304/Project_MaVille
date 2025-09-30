package ca.udem.maville.cli;

import java.util.List;



public class STPMMenu { /** 
    private final MavilleRestClient mavilleRestClient;

    private final Scanner scanner;


    public STPMMenu(MavilleRestClient mavilleRestClient, Scanner scanner) {
        this.mavilleRestClient = mavilleRestClient;
        this.scanner = scanner;
    }

    public void start() {
        stpmMenu(); // or whatever method is the main entry point
    }

    // --- STPM Profile Section ---
    private void stpmMenu() {
        while (true) {
            System.out.println("\n--- Menu STPM ---");
            System.out.println("1. Affecter automatiquement des priorités");
            System.out.println("2. Accepter ou refuser des projets de travaux");
            System.err.println("3. Voir les notifications");
            System.out.println("4. Retour au menu principal");
            System.out.print("Choisissez une action: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> affecterPrioritesAutomatiquement();
                case "2" -> evaluerProjets();
                case "3" -> {
                    consulterNotifications();
                }
                case "4" -> {
                    return;
                }
                default -> System.out.println("Option invalide.");
            }
        }
    }

    private void affecterPrioritesAutomatiquement() {
        System.out.println("\n--- Affectation automatique des priorités ---");
        try {
            List<String> updates = mavilleRestClient.mettreAJourPrioriteProbleme();

            if (updates.isEmpty()) {
                System.out.println("Aucun problème ouvert à prioriser.");
            } else {
                updates.forEach(System.out::println);
            }
            System.out.println("Priorités aléatoires affectées aux problèmes ouverts.");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'affectation des priorités : " + e.getMessage());
        }
    }

    private void evaluerProjets() {
        System.out.println("\n--- Évaluer les projets ---");
        try {
            List<CandidatureProjet> projetsEnAttente = mavilleRestClient.listerProjetsEnAttente();

            if (projetsEnAttente.isEmpty()) {
                System.out.println("Aucun projet en attente d'évaluation.");
                return;
            }

            System.out.println("Projets en attente:");
            projetsEnAttente.forEach(p -> System.out.println("ID: " + p.getId() + ", Titre: " + p.getTitre()));

            System.out.print("\nID du projet à évaluer: ");
            int id = Integer.parseInt(scanner.nextLine());
            System.out.print("Décision (1=Accepter, 2=Refuser): ");
            String decision = scanner.nextLine();

            if ("1".equals(decision)) {
                mavilleRestClient.evaluerProjet(id, true);
                System.out.println("✅ Projet approuvé!");
            } else if ("2".equals(decision)) {
                mavilleRestClient.evaluerProjet(id, false);
                System.out.println("❌ Projet refusé!");
            } else {
                System.out.println("Décision invalide.");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'évaluation des projets: " + e.getMessage());
        }
    }

    private void consulterNotifications() {
        System.out.println("\n--- Consulter les notifications ---");
        try {
            List<Notification> notifications = mavilleRestClient.consulterAllNotificationsSTPM();

            if (notifications.isEmpty()) {
                System.out.println("Aucune notification à afficher.");
                return;
            }

            System.out.println("Notifications STPM:");
            for (Notification notification : notifications) {
                System.out.println("• Type: " + notification.getNotificationType() +
                        " | Message: " + notification.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la consultation des notifications: " + e.getMessage());
        }
    } */

}