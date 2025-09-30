package ca.udem.maville.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import ca.udem.maville.enums.StatutProjet;
import ca.udem.maville.enums.TravauxType;
import ca.udem.maville.model.Candidature;
import ca.udem.maville.model.Prestataire;

@Repository
public class CandidatureProjetRepository {
    private final Map<Integer, Candidature> candidatures = new HashMap<>();

    /**
     * Saves a candidature to the repository.
     * Generates a new integer ID if the candidature is new (ID is 0).
     * @param candidature The candidature to save.
     * @return The saved candidature.
     */
    public Candidature save(Candidature candidature) {
        if (candidature.getId() == 0) {
            // Generate a simple ID for new entities
            candidature.setId(candidatures.size() + 1);
        }
        candidatures.put(candidature.getId(), candidature);
        return candidature;
    }

    /**
     * Finds a candidature by its ID.
     * @param id The ID of the candidature.
     * @return The candidature, or null if not found.
     */
    public Candidature findById(int id) {
        return candidatures.get(id);
    }

    /**
     * Retrieves all candidatures from the repository.
     * @return A list of all candidatures.
     */
    public List<Candidature> getAllCandidatures() {
        return new ArrayList<>(candidatures.values());
    }

    /**
     * Deletes a candidature by its ID.
     * @param id The ID of the candidature to delete.
     * @return True if the candidature was deleted, false otherwise.
     */
    public boolean deleteById(int id) {
        return candidatures.remove(id) != null;
    }

    /**
     * Finds candidatures by their project status.
     * @param status The status to filter by.
     * @return A list of candidatures with the specified status.
     */
    public List<Candidature> findByStatus(StatutProjet status) {
        return candidatures.values().stream()
                .filter(c -> c.getStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * Finds candidatures by the service provider's ID.
     * @param serviceProviderId The ID of the service provider.
     * @return A list of candidatures for the specified service provider.
     */
    public List<Candidature> findByServiceProviderId(Prestataire serviceProvider) {
        return candidatures.values().stream()
                .filter(c -> c.getPrestataire() != null && c.getPrestataire() == serviceProvider)
                .collect(Collectors.toList());
    }

    /**
     * Finds candidatures by their work type.
     * @param type The work type to filter by.
     * @return A list of candidatures with the specified work type.
     */
    public List<Candidature> findByWorkType(TravauxType type) {
        return candidatures.values().stream()
                .filter(c -> c.getWorkType() == type)
                .collect(Collectors.toList());
    }

    /**
     * Counts the number of candidatures with a specific status.
     * @param status The status to count.
     * @return The number of candidatures with the specified status.
     */
    public long countByStatus(StatutProjet status) {
        return candidatures.values().stream()
                .filter(c -> c.getStatus() == status)
                .count();
    }

    /**
     * Checks if a candidature exists by its ID.
     * @param id The ID to check.
     * @return True if the candidature exists, false otherwise.
     */
    public boolean existsById(int id) {
        return candidatures.containsKey(id);
    }

    /**
     * Deletes all candidatures from the repository.
     */
    public void deleteAll() {
        candidatures.clear();
    }

    /**
     * Gets the total number of candidatures in the repository.
     * @return The total count.
     */
    public long count() {
        return candidatures.size();
    }
}