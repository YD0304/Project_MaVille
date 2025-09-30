package ca.udem.maville.cli;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import ca.udem.maville.enums.TravauxType;
import ca.udem.maville.model.Abonnement;
import ca.udem.maville.model.Candidature;
import ca.udem.maville.model.Prestataire;
import ca.udem.maville.model.Problem;

public class PrestataireMenu {
    /**
    private final MavilleRestClient mavilleRestClient;
    private final Scanner scanner;

    public PrestataireMenu(MavilleRestClient mavilleRestClient, Scanner scanner) {
        this.mavilleRestClient = mavilleRestClient;
        this.scanner = scanner;
    }

    public void start() {
        menuPrestataire(); // or whatever method is the main entry point
    }

    private void menuPrestataire() {
        System.out.println("\n--- Sélection du Profil Prestataire ---");
        try {
            List<Prestataire> prestataires = mavilleRestClient.listerPrestataires();
            prestataires.forEach(p -> System.out
                    .println(p.getId() + ". " + p.getNomEntreprise() + " (" + p.getNumeroEntreprise() + ")"));
            System.out.print("Veuillez choisir votre profil (ou 'q' pour quitter) : ");
            String input = scanner.nextLine();
            if ("q".equalsIgnoreCase(input))
                return;
            try {
                int profilId = Integer.parseInt(input);
                Optional<Prestataire> prestataireOpt = prestataires.stream()
                        .filter(p -> p.getId() == profilId)
                        .findFirst();
                if (prestataireOpt.isPresent()) {
                    boucleMenuPrestataire(prestataireOpt.get());
                } else {
                    System.out.println("Profil non valide.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Erreur : Veuillez entrer un ID numérique valide.");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des prestataires: " + e.getMessage());
        }
    }

    private void boucleMenuPrestataire(Prestataire prestataireConnecte) {
        while (true) {
            System.out.println("\n--- Menu Prestataire (Connecté: " + prestataireConnecte.getNomEntreprise() + ") ---");
            System.out.println("1. Consulter les projets disponibles");
            System.out.println("2. Soumettre une candidature");
            System.out.println("3. Consulter mes projets");
            System.err.println("4. Gerer les notifications et abonnements");
            System.out.println("5. Retour au menu de sélection de profil");
            System.out.print("Choisissez une action: ");
            String choix = scanner.nextLine().trim();
            switch (choix) {
                case "1" -> handleConsultationProjetsPourPrestataire();
                case "2" -> handleSoumissionCandidature(prestataireConnecte);
                case "3" -> handleConsulterMesProjets(prestataireConnecte);
                case "4" -> handleNotifications(prestataireConnecte);
                case "5" -> {
                    return;
                }
                default -> System.out.println("Option invalide.");
            }
        }
    }

    private void handleConsultationProjetsPourPrestataire() {
        System.out.println("\n[Projets disponibles pour soumission]");
        try {
            List<Problem> problemes = mavilleRestClient.consulterProblemes();
            if (problemes.isEmpty()) {
                System.out.println("Il n'y a aucun projet disponible pour le moment.");
            } else {
                problemes.forEach(
                        p -> System.out.println("ID: " + p.getId() + " | " + p.getType() + " - " + p.getDescription()));
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des projets: " + e.getMessage());
        }
    }

    private void handleSoumissionCandidature(Prestataire prestataire) {
        System.out.println("\n--- Soumettre une candidature ---");

        try {
            int projetId;
            while (true) {
                System.out.print("ID du projet: ");
                String projetIdInput = scanner.nextLine().trim();

                if (projetIdInput.isEmpty()) {
                    System.out.println("❌ L'ID du projet ne peut pas être vide.");
                    continue;
                }

                try {
                    projetId = Integer.parseInt(projetIdInput);
                    if (projetId <= 0) {
                        System.out.println("❌ L'ID du projet doit être un nombre positif.");
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("❌ L'ID du projet doit être un nombre valide.");
                }
            }

            String dateDebut;
            LocalDate debutDate;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate today = LocalDate.now();

            while (true) {
                System.out.print("Date de début (YYYY-MM-DD): ");
                dateDebut = scanner.nextLine().trim();

                if (dateDebut.isEmpty()) {
                    System.out.println("❌ La date de début ne peut pas être vide.");
                    continue;
                }

                try {
                    debutDate = LocalDate.parse(dateDebut, formatter);
                    if (!debutDate.isAfter(today)) {
                        System.out.println("❌ La date de début doit être postérieure à aujourd'hui ("
                                + today.format(formatter) + ").");
                        continue;
                    }
                    break;
                } catch (DateTimeParseException e) {
                    System.out.println("❌ Format de date invalide. Utilisez le format YYYY-MM-DD.");
                }
            }

            String dateFin;
            LocalDate finDate;

            while (true) {
                System.out.print("Date de fin (YYYY-MM-DD): ");
                dateFin = scanner.nextLine().trim();

                if (dateFin.isEmpty()) {
                    System.out.println("❌ La date de fin ne peut pas être vide.");
                    continue;
                }

                try {
                    finDate = LocalDate.parse(dateFin, formatter);
                    if (!finDate.isAfter(debutDate)) {
                        System.out.println(
                                "❌ La date de fin doit être postérieure à la date de début (" + dateDebut + ").");
                        continue;
                    }
                    break;
                } catch (DateTimeParseException e) {
                    System.out.println("❌ Format de date invalide. Utilisez le format YYYY-MM-DD.");
                }
            }

            double cout;
            while (true) {
                System.out.print("Coût estimé: ");
                String coutInput = scanner.nextLine().trim();

                if (coutInput.isEmpty()) {
                    System.out.println("❌ Le coût estimé ne peut pas être vide.");
                    continue;
                }

                try {
                    cout = Double.parseDouble(coutInput);
                    if (cout < 0) {
                        System.out.println("❌ Le coût estimé ne peut pas être négatif.");
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("❌ Le coût estimé doit être un nombre valide.");
                }
            }

            mavilleRestClient.soumettreCandidature(projetId, dateDebut, dateFin, cout, prestataire);
            System.out.println("✅ Candidature soumise avec succès!");

        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la soumission: " + e.getMessage());
        }
    }

    private void handleConsulterMesProjets(Prestataire prestataire) {
        System.out.println("\n--- Mes Projets ---");
        try {
            List<CandidatureProjet> projets = mavilleRestClient.listerMesProjets(prestataire.getId());
            if (projets.isEmpty()) {
                System.out.println("Aucun projet trouvé pour ce prestataire.");
            } else {
                projets.forEach(p -> System.out
                        .println("ID: " + p.getId() + " | " + p.getTitre() + " - " + p.getDescription()));
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des projets: " + e.getMessage());
        }

        System.out.println("\nActions disponibles:");
        System.out.println("1. Mettre à jour l'état d'un projet");
        System.out.println("2. Mettre à jour le description d'un projet\"");
        System.out.println("3. Mettre à jour la date d'un projet");
        System.out.println("4. Retour au menu principal");
        System.out.print("Choisissez une action: ");

        String choix = scanner.nextLine().trim();
        switch (choix) {
            case "1" -> handleMiseAJourEtatProjet(prestataire);
            case "2" -> handleMiseAJourDescriptionProjet(prestataire);
            case "3" -> handleMiseAJourDateProjet(prestataire);
            case "4" -> {
                return;
            }
            default -> System.out.println("Option invalide.");
        }
    }

    private void handleMiseAJourEtatProjet(Prestataire prestataire) {
        System.out.println("\n--- Mise à jour de projet ---");
        System.out.print("ID du projet: ");
        int projetId = Integer.parseInt(scanner.nextLine());
        System.out.println("Nouvel état:");
        System.out.println("1. en cours");
        System.out.println("2. terminé");
        System.out.println("3. retardé");
        System.out.print("Choix: ");
        String etat = switch (scanner.nextLine().trim()) {
            case "1" -> "en cours";
            case "2" -> "terminé";
            case "3" -> "retardé";
            default -> "inconnu";
        };

        try {
            mavilleRestClient.changerStatutProjet(projetId, prestataire, etat);
            System.out.println("✅ État du projet mis à jour avec succès!");
        } catch (Exception e) {
            System.out.println("Erreur lors de la mise à jour: " + e.getMessage());
        }
    }

    private void handleMiseAJourDescriptionProjet(Prestataire prestataire) {
        System.out.println("\n--- Mise à jour de la description du projet ---");
        System.out.print("ID du projet: ");
        int projetId = Integer.parseInt(scanner.nextLine());

        System.out.print("Nouvelle description: ");
        String nouvelleDescription = scanner.nextLine().trim();

        if (nouvelleDescription.isEmpty()) {
            System.out.println("❌ La description ne peut pas être vide.");
            return;
        }

        try {
            mavilleRestClient.mettreAJourDescription(projetId, prestataire, nouvelleDescription);
            System.out.println("✅ Description mise à jour avec succès!");
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la mise à jour de la description: " + e.getMessage());
        }
    }

    private void handleMiseAJourDateProjet(Prestataire prestataire) {
        System.out.println("\n--- Mise à jour de la date de fin du projet ---");

        // Validate project ID
        int projetId;
        while (true) {
            System.out.print("ID du projet: ");
            String projetIdInput = scanner.nextLine().trim();

            if (projetIdInput.isEmpty()) {
                System.out.println("❌ L'ID du projet ne peut pas être vide.");
                continue;
            }

            try {
                projetId = Integer.parseInt(projetIdInput);
                if (projetId <= 0) {
                    System.out.println("❌ L'ID du projet doit être un nombre positif.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("❌ L'ID du projet doit être un nombre valide.");
            }
        }

        // Validate new end date
        LocalDate nouvelleDateFin;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();

        while (true) {
            System.out.print("Nouvelle date de fin (YYYY-MM-DD): ");
            String dateFinInput = scanner.nextLine().trim();

            if (dateFinInput.isEmpty()) {
                System.out.println("❌ La date de fin ne peut pas être vide.");
                continue;
            }

            try {
                nouvelleDateFin = LocalDate.parse(dateFinInput, formatter);
                if (nouvelleDateFin.isBefore(today)) {
                    System.out.println(
                            "❌ La date de fin doit être postérieure à aujourd'hui (" + today.format(formatter) + ").");
                    continue;
                }
                break;
            } catch (DateTimeParseException e) {
                System.out.println("❌ Format de date invalide. Utilisez le format YYYY-MM-DD.");
            }
        }

        try {
            mavilleRestClient.mettreAJourDateFin(projetId, prestataire, nouvelleDateFin);
            System.out.println("✅ Date de fin mise à jour avec succès!");
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la mise à jour de la date: " + e.getMessage());
        }
    }

    private void handleNotifications(Prestataire prestataire) {
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
                    case "1" -> consulterAbonnements(prestataire);
                    case "2" -> consulterNotifications(prestataire);
                    case "3" -> ajouterAbonnement(prestataire);
                    case "4" -> supprimerAbonnements(prestataire);
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

    private void consulterNotifications(Prestataire prestataire) {
        System.out.println("\n--- Consulter Notifications ---");
        System.out.println("1. Toutes les notifications");
        System.out.println("2. Notifications par type");
        System.out.print("Choisissez une option (1-2): ");

        String choix = scanner.nextLine().trim();

        try {
            List<Notification> notifications;

            if ("1".equals(choix)) {
                notifications = mavilleRestClient.consulterAllNotifications(prestataire.getId());
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

                notifications = mavilleRestClient.consulterNotificationsByType(prestataire.getId(), type);
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
                    List<Abonnement> abonnements = mavilleRestClient.getAbonnementsPrestataire(prestataire.getId());
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

    private void consulterAbonnements(Prestataire prestataire) {
        try {
            List<Abonnement> abonnements = mavilleRestClient.getAbonnementsPrestataire(prestataire.getId());

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

    private void ajouterAbonnement(Prestataire prestataire) {
        try {
            System.out.println("\nTypes d'abonnement disponibles:");
            System.out.println("1. Abonnement à un quartier");
            System.out.println("2. Abonnement à un type de problème");
            System.out.print("Choisissez un type (1-2): ");

            String typeChoice = scanner.nextLine().trim();
            String type;
            String valeur;

            if ("1".equals(typeChoice)) {
                type = "QUARTIER";
                System.out.print("Entrez le nom du quartier: ");
                valeur = scanner.nextLine().trim();
            } else if ("2".equals(typeChoice)) {
                type = "TYPE_PROBLEME";
                System.out.println("Types de problèmes disponibles:");
                ProblemType problemeType = CLIMain.demanderProblemeType(scanner);
                if (problemeType == null) {
                    System.out.println("Aucun type de problème sélectionné. Abonnement annulé.");
                    return;
                }
                valeur = problemeType.name();
            } else {
                System.out.println("Choix invalide.");
                return;
            }

            if (valeur.isEmpty()) {
                System.out.println("La valeur ne peut pas être vide.");
                return;
            }

            mavilleRestClient.ajouterAbonnementPrestataire(prestataire.getId(), type, valeur);
            System.out.println("✅ Abonnement créé avec succès!");

        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la création de l'abonnement : " + e.getMessage());
        }
    }

    private void supprimerAbonnements(Prestataire prestataire) {
        try {
            List<Abonnement> abonnements = mavilleRestClient.getAbonnementsPrestataire(prestataire.getId());

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

            System.out.print("\nEntrez l'ID de l'abonnement à supprimer (ou 'q' pour annuler) : ");
            String choixStr = scanner.nextLine().trim();

            if ("q".equalsIgnoreCase(choixStr)) {
                System.out.println("Annulation de la suppression.");
                return;
            }

            try {
                int idToDelete = Integer.parseInt(choixStr);

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
    } */
}
