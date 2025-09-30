package ca.udem.maville.repository;

import org.springframework.stereotype.Repository;
@Repository
public class PrestataireDAO {
    /**
    private static final String FILE_PATH = "prestataires.json";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<Prestataire> prestataires = new ArrayList<>();
    private int prestataireIdCounter = 1;

    public PrestataireDAO() {
        chargerDonnees();
        if (prestataires.isEmpty()) {
            initialiserDonneesParDefaut();
            sauvegarderDonnees();
        }
    }

    public List<Prestataire> listerTous() {
        return new ArrayList<>(prestataires);
    }


    public Optional<Prestataire> trouverParId(int id) {
        return prestataires.stream()
                .filter(p -> p.getId() == id)
                .findFirst();
    }

    public void sauvegarderPrestataire(Prestataire prestataire) {
        if (prestataire.getId() <= 0) {
            prestataire.setId(prestataireIdCounter++);
        } else if (prestataire.getId() >= prestataireIdCounter) {
            prestataireIdCounter = prestataire.getId() + 1;
        }

        trouverParId(prestataire.getId()).ifPresent(prestataires::remove);
        prestataires.add(prestataire);
        sauvegarderDonnees();
    }

    public void supprimerPrestataire(int id) {
        prestataires.removeIf(p -> p.getId() == id);
        sauvegarderDonnees();
    }


    public void addNotification(int prestataireId, Notification notification) {
        trouverParId(prestataireId).ifPresentOrElse(prestataire -> {
            if (!prestataire.getNotifications().contains(notification)) {
                prestataire.addNotification(notification);
                sauvegarderPrestataire(prestataire);
            }
        }, () -> System.err.println("Prestataire avec ID " + prestataireId + " non trouvé."));
    }

    public List<Notification> getNotifications(int prestataireId) {
        return trouverParId(prestataireId)
                .map(Prestataire::getNotifications)
                .orElseGet(ArrayList::new);
    }

    // === Méthodes privées ===

    private void chargerDonnees() {
        File file = new File(FILE_PATH);
        if (!file.exists())
            return;

        try {
            List<Prestataire> charges = objectMapper.readValue(file, new TypeReference<>() {
            });
            prestataires.clear();
            prestataires.addAll(charges);
            prestataireIdCounter = charges.stream()
                    .mapToInt(Prestataire::getId)
                    .max()
                    .orElse(0) + 1;
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement des prestataires : " + e.getMessage());
        }
    }

    private void sauvegarderDonnees() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), prestataires);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des prestataires : " + e.getMessage());
        }
    }
    */

}