package ca.udem.maville.api.client;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    // Work-related methods
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

    // Getter for current user
    public User getCurrentUser() {
        return currentUser;
    }

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
}