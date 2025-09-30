package ca.udem.maville.repository;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import ca.udem.maville.enums.StatutProjet;
import ca.udem.maville.enums.TravauxType;
import ca.udem.maville.model.Work;


@Repository
public class WorkRepository {
    private final TravauxAPIRepository travauxAPIRepository;
    private final CandidatureProjetRepository candidatureRepository;

    private static final String FILE_PATH = "work.json";
    private ObjectMapper objectMapper;
    private List<Work> works;

    public WorkRepository() {
        this.travauxAPIRepository = new TravauxAPIRepository();
        this.candidatureRepository = new CandidatureProjetRepository();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules(); // Support pour LocalDate
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Formatage lisible
        this.works = new ArrayList<>();
        loadWorks(); // Charger les données existantes au démarrage
    }

    /**
     * Charge les travaux à partir du fichier JSON.
     */
    private void loadWorks() {
        try {
            File file = new File(FILE_PATH);
            if (file.exists()) {
                works = objectMapper.readValue(file, new TypeReference<List<Work>>() {});
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement des travaux depuis le fichier JSON : " + e.getMessage());
        }
    }

    /**
     * Sauvegarde la liste de travaux dans un fichier JSON.
     */
    private void saveWorks() {
        try {
            objectMapper.writeValue(new File(FILE_PATH), works);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des travaux dans le fichier JSON : " + e.getMessage());
        }
    }

    /**
     * Met à jour et retourne tous les travaux à partir des sources de données.
     * Les données sont également sauvegardées localement.
     */
    public List<Work> updateAndGetAllWork() {
        List<Work> allWork = new ArrayList<>();
        // Récupérer les travaux de l'API
        travauxAPIRepository.filterTravauxExterneList(null,null).stream()
                .map(Work::fromMontrealAPI)
                .forEach(allWork::add);

        // Récupérer les travaux des candidatures locales
        candidatureRepository.getAllCandidatures().stream()
                .map(Work::fromProject)
                .forEach(allWork::add);

        this.works = allWork; // Mettre à jour la liste interne
        saveWorks();          // Sauvegarder la nouvelle liste dans le fichier
        
        return new ArrayList<>(this.works);
    }
    
    /**
     * Récupère tous les travaux en mémoire.
     */
    public List<Work> getCachedWorks() {
        return new ArrayList<>(works);
    }

    /**
     * Deletes all external (API) work from the repository.
     */
    public void deleteAllExternalWork() {
        // Cette méthode doit maintenant mettre à jour la liste locale
        works.removeIf(work -> "MONTREAL_API".equals(work.getSource()));
        saveWorks(); // Sauvegarder la liste mise à jour
    }

    public List<Work> findByStatus(StatutProjet status) {
        return works.stream()
                .filter(work -> work.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Work> findByCategory(String type) {
        return works.stream()
                .filter(work -> work.getCategory() == type)
                .collect(Collectors.toList());
    }

    public List<Work> findByNeighbourhood(String neighbourhood) {
        return works.stream()
                .filter(work -> work.getBorough() == neighbourhood)
                .collect(Collectors.toList());
    }

    public List<Work> findByStreet(String street) {
        return works.stream()
                .filter(work -> work.getStreet() == street)
                .collect(Collectors.toList());
    }

    public List<Work> findByDateBetween(LocalDate start, LocalDate end) {
        return works.stream()
                .filter(work -> work.getStartDate() != null)
                .filter(work -> {
                    try {
                        LocalDate workDate = work.getStartDate();
                        return (workDate.isEqual(start) || workDate.isAfter(start)) &&
                               (workDate.isEqual(end) || workDate.isBefore(end));
                    } catch (Exception e) {
                        return false; // Ignore parsing errors
                    }
                })
                .collect(Collectors.toList());
    }

    public List<Work> findByPrestataireId(int prestataireId) {
        return works.stream()
                .filter(work -> work.getServiceProvider() != null && work.getServiceProvider().getId() == prestataireId)
                .collect(Collectors.toList());
    }
}