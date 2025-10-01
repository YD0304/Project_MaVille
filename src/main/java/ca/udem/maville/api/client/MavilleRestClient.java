package ca.udem.maville.api.client;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.udem.maville.enums.StatutProjet;
import ca.udem.maville.model.Candidature;
import ca.udem.maville.model.Prestataire;
import ca.udem.maville.model.Problem;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import ca.udem.maville.model.User;
import ca.udem.maville.model.Work;

public class MavilleRestClient {
    private final String baseUrl;
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private User currentUser;

    public MavilleRestClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules(); // Support for Java 8 Date/Time API
    }

    // Getter for current user
    public User getCurrentUser() {
        return currentUser;
    }
    // User authentication methods
    public boolean login(String username, String password) throws IOException {
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(baseUrl + "/api/users/login")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String json = response.body().string();
                currentUser = objectMapper.readValue(json, User.class);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean register(User user) throws IOException {
        String userJson = objectMapper.writeValueAsString(user);
        RequestBody body = RequestBody.create(
                userJson, 
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(baseUrl + "/api/users/register")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    /////////////////////////////////Work-related methods////////////////////////////////
    public List<Work> getInProgressWorks() throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/api/works/in_progress_works")
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            
            String json = response.body().string();
            return objectMapper.readValue(json, new TypeReference<List<Work>>(){});
        }
    }

    public List<Work> getUpcomingWorks() throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/api/works/upcoming_works")
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            
            String json = response.body().string();
            return objectMapper.readValue(json, new TypeReference<List<Work>>(){});
        }
    }

    public List<Work> getWorksByType(String type) throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/api/works/filter?type=" + java.net.URLEncoder.encode(type, "UTF-8"))
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            
            String json = response.body().string();
            return objectMapper.readValue(json, new TypeReference<List<Work>>(){});
        }
    }

    public List<Work> getWorksByNeighbourhood(String neighbourhood) throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/api/works/filter_by_neighbourhood?neighbourhood=" + java.net.URLEncoder.encode(neighbourhood, "UTF-8"))
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            
            String json = response.body().string();
            return objectMapper.readValue(json, new TypeReference<List<Work>>(){});
        }
    }

    public List<Work> getWorksByStreet(String street) throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/api/works/filter_by_street?street=" + java.net.URLEncoder.encode(street, "UTF-8"))
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            
            String json = response.body().string();
            return objectMapper.readValue(json, new TypeReference<List<Work>>(){});
        }
    }

    /////////////////////////////////Problem-related methods////////////////////////////////

    public Problem submitProblem(Problem problem) throws IOException {
        String problemJson = objectMapper.writeValueAsString(problem);
        RequestBody body = RequestBody.create(
                problemJson, 
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(baseUrl + "/api/problems/submit")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String json = response.body().string();
                return objectMapper.readValue(json, Problem.class);
            } else {
                throw new IOException("Failed to submit problem: " + response.message());
            }
        }
    }

    public List<Problem> getAllProblems() throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/api/problems/all")
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            
            String json = response.body().string();
            return objectMapper.readValue(json, new TypeReference<List<Problem>>(){});
        }
    }

    public List<Problem> getMyReportedProblems(User resident) throws IOException {
        String residentJson = objectMapper.writeValueAsString(resident);
        RequestBody body = RequestBody.create(
                residentJson, 
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(baseUrl + "/api/problems/my_reported_problems")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String json = response.body().string();
                return objectMapper.readValue(json, new TypeReference<List<Problem>>(){});
            } else {
                throw new IOException("Failed to fetch reported problems: " + response.message());
            }
        }
    }

    public List<Problem> getProblemsNotAssigned() throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/api/problems/problems_not_assigned")
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            
            String json = response.body().string();
            return objectMapper.readValue(json, new TypeReference<List<Problem>>(){});
        }
    }

    public Problem assignProblemPriority(int problemId, String priorite) throws IOException {
        String url = baseUrl + "/api/problems/assign_problem_priority" +
                "?problemId=" + problemId +
                "&priorite=" + java.net.URLEncoder.encode(priorite, "UTF-8");

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create("", MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String json = response.body().string();
                return objectMapper.readValue(json, Problem.class);
            } else {
                throw new IOException("Failed to assign problem priority: " + response.message());
            }
        }
    }

    //////////////////////////// project-related methods ///////////////////


    public Candidature submitProposal(int problemId, String title, String description, 
                                String startDate, String endDate, double cost, 
                                Prestataire prestataire) throws IOException {
    
    // Build URL with query parameters using URLEncoder, matching your existing pattern
    String url = baseUrl + "/api/projects/" + problemId + "/proposals" +
            "?title=" + java.net.URLEncoder.encode(title, "UTF-8") +
            "&description=" + java.net.URLEncoder.encode(description, "UTF-8") +
            "&startDate=" + java.net.URLEncoder.encode(startDate, "UTF-8") +
            "&endDate=" + java.net.URLEncoder.encode(endDate, "UTF-8") +
            "&cost=" + cost;

    String prestataireJson = objectMapper.writeValueAsString(prestataire);
    RequestBody body = RequestBody.create(
            prestataireJson, 
            MediaType.parse("application/json")
    );

    Request request = new Request.Builder()
            .url(url)
            .post(body)
            .build();

    try (Response response = client.newCall(request).execute()) {
        if (response.isSuccessful()) {
            String json = response.body().string();
            return objectMapper.readValue(json, Candidature.class);
        } else {
            throw new IOException("Failed to submit proposal: " + response.message());
        }
    }
}

public List <Candidature> viewAllProposals() throws IOException {
    Request request = new Request.Builder()
            .url(baseUrl + "/api/projects/proposals")
            .build();
    
    try (Response response = client.newCall(request).execute()) {
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        
        String json = response.body().string();
        return objectMapper.readValue(json, new TypeReference<List<Candidature>>() {});
    }
}

public Work updateWorkStatus(int projectId, Prestataire prestataire, StatutProjet status) throws IOException {
    String url = baseUrl + "/api/projects/" + projectId + "/update_status" +
            "?status=" + status.toString();

    String prestataireJson = objectMapper.writeValueAsString(prestataire);
    RequestBody body = RequestBody.create(
            prestataireJson, 
            MediaType.parse("application/json")
    );

    Request request = new Request.Builder()
            .url(url)
            .put(body)
            .build();

    try (Response response = client.newCall(request).execute()) {
        if (response.isSuccessful()) {
            String json = response.body().string();
            return objectMapper.readValue(json, Work.class);
        } else {
            throw new IOException("Failed to update work status: " + response.message());
        }
    }
}

public Work updateWorkDescription(int projectId, Prestataire prestataire, String description) throws IOException {
    String url = baseUrl + "/api/projects/" + projectId + "/update_description" +
            "?description=" + java.net.URLEncoder.encode(description, "UTF-8");

    String prestataireJson = objectMapper.writeValueAsString(prestataire);
    RequestBody body = RequestBody.create(
            prestataireJson, 
            MediaType.parse("application/json")
    );

    Request request = new Request.Builder()
            .url(url)
            .put(body)
            .build();

    try (Response response = client.newCall(request).execute()) {
        if (response.isSuccessful()) {
            String json = response.body().string();
            return objectMapper.readValue(json, Work.class);
        } else {
            throw new IOException("Failed to update work description: " + response.message());
        }
    }
}

public Work updateWorkEndDate(int projectId, Prestataire prestataire, String newDate) throws IOException {
    String url = baseUrl + "/api/projects/" + projectId + "/update_end-date" +
            "?newDate=" + java.net.URLEncoder.encode(newDate, "UTF-8");

    String prestataireJson = objectMapper.writeValueAsString(prestataire);
    RequestBody body = RequestBody.create(
            prestataireJson, 
            MediaType.parse("application/json")
    );

    Request request = new Request.Builder()
            .url(url)
            .put(body)
            .build();

    try (Response response = client.newCall(request).execute()) {
        if (response.isSuccessful()) {
            String json = response.body().string();
            return objectMapper.readValue(json, Work.class);
        } else {
            throw new IOException("Failed to update work end date: " + response.message());
        }
    }
}

public Work evaluateProposal(int projectId, StatutProjet statut) throws IOException {
    String url = baseUrl + "/api/stpm/proposals/" + projectId + "/evaluate" +
            "?status=" + statut.toString();

    Request request = new Request.Builder()
            .url(url)
            .post(RequestBody.create("", MediaType.parse("application/json")))
            .build();

    try (Response response = client.newCall(request).execute()) {
        return (response.isSuccessful()) ? 
            objectMapper.readValue(response.body().string(), Work.class) : null;
    }
}
}