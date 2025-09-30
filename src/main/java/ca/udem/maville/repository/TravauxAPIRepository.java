package ca.udem.maville.repository;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import ca.udem.maville.model.MontrealAPIWork;

import org.springframework.stereotype.Repository;
@Repository
public class TravauxAPIRepository {
    private static final String BASE_API_URL = "https://donnees.montreal.ca/api/3/action/datastore_search?resource_id=cc41b532-f12d-40fb-9f55-eb58c9a2b12b";
    private static final String FILE_PATH = "travaux_API.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final List<MontrealAPIWork> travaux = new ArrayList<>();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public TravauxAPIRepository() {
        loadData();
    }

    private void loadData() {
        try {
            File file = new File(FILE_PATH);
            if (file.exists()) {
                List<MontrealAPIWork> datas = objectMapper.readValue(file, new TypeReference<List<MontrealAPIWork>>() {
                });
                travaux.clear();
                travaux.addAll(datas);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement des travaux : " + e.getMessage());
        }
    }

    private void saveData() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), travaux);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des travaux : " + e.getMessage());
        }
    }

    public List<MontrealAPIWork> filterTravauxExterneList(String filterKey, String filterValue) {
        String finalUrl = BASE_API_URL + "&limit=100";
        try {
            if (filterKey != null && !filterKey.isEmpty() && filterValue != null && !filterValue.isEmpty()) {
                String filterJson = String.format("{\"%s\": \"%s\"}", filterKey, filterValue);
                String encodedFilter = URLEncoder.encode(filterJson, StandardCharsets.UTF_8);
                finalUrl += "&filters=" + encodedFilter;
            }
    
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(finalUrl))
                    .build();
    
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    
            System.out.println("API Response Body: " + response.body());
    
            if (response.statusCode() != 200) {
                throw new RuntimeException("External API error: " + response.statusCode());
            }
    
            travaux.clear();
            List<MontrealAPIWork> externes = parseJsonResponseWithJackson(response.body());
            System.out.println("Number of records parsed: " + externes.size());
    
            // La boucle est maintenant plus simple et plus efficace
            for (MontrealAPIWork t : externes) {
                travaux.add(t);
            }
    
            saveData();
    
            return new ArrayList<>(externes);
    
        } catch (IOException | InterruptedException e) {
            System.err.println("Erreur lors de la communication avec l'API externe : " + e.getMessage());
            return new ArrayList<>();
        } catch (RuntimeException e) {
            System.err.println("Erreur d'exécution : " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<MontrealAPIWork> parseJsonResponseWithJackson(String jsonResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode recordsNode = rootNode.path("result").path("records");

            CollectionType listType = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, MontrealAPIWork.class);

            return objectMapper.readValue(objectMapper.treeAsTokens(recordsNode), listType);

        } catch (Exception e) {
            System.err.println("Erreur lors de l'analyse JSON : " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<MontrealAPIWork> getAllAPIWorks() {
        return new ArrayList<>(travaux);
    }

    public void supprimerTousLesTravauxExternes() {
        travaux.clear();
        saveData();
    }
}
