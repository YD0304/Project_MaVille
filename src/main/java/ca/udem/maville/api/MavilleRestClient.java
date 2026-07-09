package ca.udem.maville.api;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import ca.udem.maville.model.*;
import ca.udem.maville.security.JwtAuthResponse;

import okhttp3.*;

public class MavilleRestClient {
    private final String baseUrl;
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private String jwtToken; 

    public MavilleRestClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // support LocalDate
    }
  
    // Add these to MavilleRestClient.java

public JwtAuthResponse login(String email, String password) throws IOException {
    ObjectNode body = objectMapper.createObjectNode();
    body.put("email", email);
    body.put("password", password);
    Request request = new Request.Builder()
        .url(baseUrl + "/api/auth/login")
        .post(RequestBody.create(body.toString(), MediaType.parse("application/json")))
        .build();
    try (Response response = client.newCall(request).execute()) {
        if (!response.isSuccessful()) throw new IOException("Login failed");
        JwtAuthResponse authResponse = objectMapper.readValue(response.body().string(), JwtAuthResponse.class);
        this.jwtToken = authResponse.getAccessToken();   // ← store token
        return authResponse;
    }
}

private Request.Builder addAuthHeader(Request.Builder builder) {
    if (jwtToken != null) {
        builder.header("Authorization", "Bearer " + jwtToken);
    }
    return builder;
}


public Resident registerResident(Resident resident) throws IOException {

    String json = objectMapper.writeValueAsString(resident);
    Request request = new Request.Builder()
        .url(baseUrl + "/api/resident/register")
        .post(RequestBody.create(json, MediaType.parse("application/json")))
        .build();
    try (Response response = client.newCall(request).execute()) {
        if (!response.isSuccessful()) throw new IOException("Registration failed");
        return objectMapper.readValue(response.body().string(), Resident.class);
    }
}

public Provider registerProvider(Provider provider) throws IOException {
    String json = objectMapper.writeValueAsString(provider);
    Request request = new Request.Builder()
        .url(baseUrl + "/api/provider/register")
        .post(RequestBody.create(json, MediaType.parse("application/json")))
        .build();
    try (Response response = client.newCall(request).execute()) {
        if (!response.isSuccessful()) throw new IOException("Registration failed");
        return objectMapper.readValue(response.body().string(), Provider.class);
    }
}

public Resident getResidentByEmail(String email) throws IOException {
    String url = baseUrl + "/api/residents/by-email?email=" + java.net.URLEncoder.encode(email, "UTF-8");
    Request request = new Request.Builder().url(url).get().build();
    try (Response response = client.newCall(request).execute()) {
        if (!response.isSuccessful()) throw new IOException("Resident not found");
        return objectMapper.readValue(response.body().string(), Resident.class);
    }
}

public Provider getProviderByCompanyNumber(String companyNumber) throws IOException {
    String url = baseUrl + "/api/providers/by-company-number?companyNumber=" 
            + java.net.URLEncoder.encode(companyNumber, "UTF-8");
    Request request = new Request.Builder().url(url).get().build();
    try (Response response = client.newCall(request).execute()) {
        if (!response.isSuccessful()) throw new IOException("Provider not found");
        return objectMapper.readValue(response.body().string(), Provider.class);
    }
}

    // ─── Problem methods (unchanged, still valid) ─────────────────────────────

    public Problem submitProblem(Problem problem) throws IOException {
        Resident resident = problem.getResident();
        if (resident == null) throw new IllegalArgumentException("Problem must have a Resident.");

        ObjectNode dto = objectMapper.createObjectNode();
        dto.put("residentId", resident.getId());
        dto.put("neighbourhood", problem.getNeighbourhood());
        dto.put("street", problem.getStreet());
        dto.put("type", problem.getType() != null ? problem.getType().name() : null);
        dto.put("description", problem.getDescription());

        String json = objectMapper.writeValueAsString(dto);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(baseUrl + "/api/problems/report_problem")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed: " + response);
            return objectMapper.readValue(response.body().string(), Problem.class);
        }
    }

    public List<Problem> getAllProblems() throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/api/problems/all_reported_problems")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed: " + response);
            return objectMapper.readValue(response.body().string(), new TypeReference<List<Problem>>(){});
        }
    }

    public List<Problem> getMyReportedProblems(Long residentId) throws IOException {
        String url = baseUrl + "/api/problems/my_reported_problems?residentId=" + residentId;
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed: " + response);
            return objectMapper.readValue(response.body().string(), new TypeReference<List<Problem>>(){});
        }
    }

    public List<Problem> getProblemsNotAssigned() throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/api/problems/problems_not_assigned")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed: " + response);
            return objectMapper.readValue(response.body().string(), new TypeReference<List<Problem>>(){});
        }
    }

    public List<Problem> getProblemsAssigned() throws IOException {
    Request request = new Request.Builder()
            .url(baseUrl + "/api/problems/problems_assigned")
            .get()
            .build();
    try (Response response = client.newCall(request).execute()) {
        if (!response.isSuccessful()) throw new IOException("Failed: " + response);
        return objectMapper.readValue(response.body().string(), new TypeReference<List<Problem>>(){});
    }
}

    public Problem assignProblemPriority(int problemId, String priorite) throws IOException {
        String url = baseUrl + "/api/problems/assign_problem_priority?problemId=" + problemId
                + "&priorite=" + java.net.URLEncoder.encode(priorite, "UTF-8");
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create("", MediaType.parse("application/json")))
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed: " + response);
            return objectMapper.readValue(response.body().string(), Problem.class);
        }
    }

    // ─── Project (proposal & work) methods for Providers ──────────────────────

    public Project submitProposal(Long problemId, String title, String description,
                                  double proposedCost, LocalDate proposedStartDate,
                                  LocalDate proposedEndDate, String providerCompanyNumber) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + "/api/projects/submit").newBuilder();
        urlBuilder.addQueryParameter("problemId", String.valueOf(problemId));
        urlBuilder.addQueryParameter("title", title);
        urlBuilder.addQueryParameter("description", description);
        urlBuilder.addQueryParameter("proposedCost", String.valueOf(proposedCost));
        urlBuilder.addQueryParameter("proposedStartDate", proposedStartDate.toString());
        urlBuilder.addQueryParameter("proposedEndDate", proposedEndDate.toString());
        urlBuilder.addQueryParameter("providerCompanyNumber", providerCompanyNumber);

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .post(RequestBody.create("", MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed to submit proposal: " + response);
            return objectMapper.readValue(response.body().string(), Project.class);
        }
    }

    public List<Project> getMyProposals(String providerCompanyNumber) throws IOException {
        String url = baseUrl + "/api/projects/my-proposals?providerCompanyNumber="
                + java.net.URLEncoder.encode(providerCompanyNumber, "UTF-8");
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed: " + response);
            return objectMapper.readValue(response.body().string(), new TypeReference<List<Project>>(){});
        }
    }

    public Project updateProposalDescription(Long projectId, String providerCompanyNumber, String newDescription) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + "/api/projects/" + projectId + "/description").newBuilder();
        urlBuilder.addQueryParameter("providerCompanyNumber", providerCompanyNumber);
        urlBuilder.addQueryParameter("newDescription", newDescription);

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .put(RequestBody.create("", MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed: " + response);
            return objectMapper.readValue(response.body().string(), Project.class);
        }
    }

    public Project updateProposalEndDate(Long projectId, String providerCompanyNumber, LocalDate newEndDate) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + "/api/projects/" + projectId + "/end-date").newBuilder();
        urlBuilder.addQueryParameter("providerCompanyNumber", providerCompanyNumber);
        urlBuilder.addQueryParameter("newEndDate", newEndDate.toString());

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .put(RequestBody.create("", MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed: " + response);
            return objectMapper.readValue(response.body().string(), Project.class);
        }
    }

    public Project startWork(Long projectId, String providerCompanyNumber) throws IOException {
        return updateWorkStatus(projectId, providerCompanyNumber, "start");
    }

    public Project delayWork(Long projectId, String providerCompanyNumber) throws IOException {
        return updateWorkStatus(projectId, providerCompanyNumber, "delay");
    }

    public Project resumeWork(Long projectId, String providerCompanyNumber) throws IOException {
        return updateWorkStatus(projectId, providerCompanyNumber, "resume");
    }

    public Project completeWork(Long projectId, String providerCompanyNumber, Double actualCost) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + "/api/projects/" + projectId + "/complete").newBuilder();
        urlBuilder.addQueryParameter("providerCompanyNumber", providerCompanyNumber);
        if (actualCost != null) urlBuilder.addQueryParameter("actualCost", String.valueOf(actualCost));

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .put(RequestBody.create("", MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed: " + response);
            return objectMapper.readValue(response.body().string(), Project.class);
        }
    }

    private Project updateWorkStatus(Long projectId, String providerCompanyNumber, String action) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + "/api/projects/" + projectId + "/" + action).newBuilder();
        urlBuilder.addQueryParameter("providerCompanyNumber", providerCompanyNumber);

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .put(RequestBody.create("", MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed to " + action + " work: " + response);
            return objectMapper.readValue(response.body().string(), Project.class);
        }
    }

    public List<Project> getProjectsByStatus(ProjectStatus status) throws IOException {
    String url = baseUrl + "/api/projects/status?status=" + status.name();
    Request request = new Request.Builder().url(url).get().build();
    try (Response response = client.newCall(request).execute()) {
        if (!response.isSuccessful()) throw new IOException("Failed: " + response);
        return objectMapper.readValue(response.body().string(), new TypeReference<List<Project>>(){});
    }
}

public List<Project> getProjectsByDateRange(LocalDate start, LocalDate end) throws IOException {
    String url = baseUrl + "/api/projects/date-range?start=" + start + "&end=" + end;
    Request request = new Request.Builder().url(url).get().build();
    try (Response response = client.newCall(request).execute()) {
        if (!response.isSuccessful()) throw new IOException("Failed: " + response);
        return objectMapper.readValue(response.body().string(), new TypeReference<List<Project>>(){});
    }
}

public List<Project> getProjectsByStreet(String street) throws IOException {
    String url = baseUrl + "/api/projects/filter?street=" + java.net.URLEncoder.encode(street, "UTF-8");
    Request request = new Request.Builder().url(url).get().build();
    try (Response response = client.newCall(request).execute()) {
        if (!response.isSuccessful()) throw new IOException("Failed: " + response);
        return objectMapper.readValue(response.body().string(), new TypeReference<List<Project>>(){});
    }
}

public List<Project> getProjectsByType(WorkType type) throws IOException {
    String url = baseUrl + "/api/projects/filter?type=" + type.name();
    Request request = new Request.Builder().url(url).get().build();
    try (Response response = client.newCall(request).execute()) {
        if (!response.isSuccessful()) throw new IOException("Failed: " + response);
        return objectMapper.readValue(response.body().string(), new TypeReference<List<Project>>(){});
    }
}

    public List<Project> getProjectsByPriority(String priority) throws IOException {
        String url = baseUrl + "/api/projects/filter?priority=" + java.net.URLEncoder.encode(priority, "UTF-8");
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed: " + response);
            return objectMapper.readValue(response.body().string(), new TypeReference<List<Project>>(){});
        }
    }

    public List<Project> getMyProjects() throws IOException {
        Request request = addAuthHeader(new Request.Builder()
                .url(baseUrl + "/api/projects/my")
                .get())
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed: " + response);
            return objectMapper.readValue(response.body().string(), new TypeReference<List<Project>>(){});
        }
    }

    // ─── STPM actions ─────────────────────────────────────────────────────────

    public List<Project> getSubmittedProposals() throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/api/projects/submitted")
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed: " + response);
            return objectMapper.readValue(response.body().string(), new TypeReference<List<Project>>(){});
        }
    }

    public Project acceptProposal(Long projectId) throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/api/projects/" + projectId + "/accept")
                .post(RequestBody.create("", MediaType.parse("application/json")))
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed to accept: " + response);
            return objectMapper.readValue(response.body().string(), Project.class);
        }
    }

    public Project rejectProposal(Long projectId) throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/api/projects/" + projectId + "/reject")
                .post(RequestBody.create("", MediaType.parse("application/json")))
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed to reject: " + response);
            return objectMapper.readValue(response.body().string(), Project.class);
        }
    }

    public void reportProject(Long projectId) throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/api/projects/" + projectId + "/report")
                .post(RequestBody.create("", MediaType.parse("application/json")))
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed to report: " + response);
        }
    }

    public Problem linkSignalToProblem(Long signalId, Long parentProblemId) throws IOException {
        String url = baseUrl + "/api/problems/link_signal?signalId=" + signalId
                + "&parentProblemId=" + parentProblemId;
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create("", MediaType.parse("application/json")))
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed to link: " + response);
            return objectMapper.readValue(response.body().string(), Problem.class);
        }
    }

    // ─── Resident viewing projects (filters) ──────────────────────────────────
    // Note: You may need to add endpoints in ProjectController for these.
    // For now, we'll assume endpoints exist or you can use generic getAllProjects.

    public List<Project> getAllProjects() throws IOException {
        // If you have an endpoint /api/projects/all, use it. Otherwise, adapt.
        Request request = new Request.Builder()
                .url(baseUrl + "/api/projects/all")
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed: " + response);
            return objectMapper.readValue(response.body().string(), new TypeReference<List<Project>>(){});
        }
    }

    // Example: filter by neighbourhood (if endpoint exists)
    public List<Project> getProjectsByNeighbourhood(String neighbourhood) throws IOException {
        String url = baseUrl + "/api/projects/filter?neighbourhood="
                + java.net.URLEncoder.encode(neighbourhood, "UTF-8");
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed: " + response);
            return objectMapper.readValue(response.body().string(), new TypeReference<List<Project>>(){});
        }
    }

    // ─── Subscriptions (Resident) ─────────────────────────────────────────────

    public ResidentSubscription subscribeResident(Long residentId, AbonnementType type, String value) throws IOException {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("residentId", residentId);
        body.put("type", type.name());
        body.put("value", value);
        body.put("active", true);

        Request request = new Request.Builder()
                .url(baseUrl + "/api/subscriptions/residents")
                .post(RequestBody.create(body.toString(), MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed: " + response);
            return objectMapper.readValue(response.body().string(), ResidentSubscription.class);
        }
    }

    public List<ResidentSubscription> getResidentSubscriptions(Long residentId) throws IOException {
        String url = baseUrl + "/api/subscriptions/residents?residentId=" + residentId;
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed: " + response);
            return objectMapper.readValue(response.body().string(), new TypeReference<List<ResidentSubscription>>(){});
        }
    }

    public void unsubscribeResident(Long subscriptionId) throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/api/subscriptions/residents/" + subscriptionId)
                .delete()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed: " + response);
        }
    }

    // ─── Subscriptions (Provider) ─────────────────────────────────────────────

    public ProviderSubscription subscribeProvider(String companyNumber, AbonnementType type, String value) throws IOException {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("providerCompanyNumber", companyNumber);
        body.put("type", type.name());
        body.put("value", value);
        body.put("active", true);

        Request request = new Request.Builder()
                .url(baseUrl + "/api/subscriptions/providers")
                .post(RequestBody.create(body.toString(), MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed: " + response);
            return objectMapper.readValue(response.body().string(), ProviderSubscription.class);
        }
    }

    public List<ProviderSubscription> getProviderSubscriptions(String companyNumber) throws IOException {
        String url = baseUrl + "/api/subscriptions/providers?companyNumber="
                + java.net.URLEncoder.encode(companyNumber, "UTF-8");
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed: " + response);
            return objectMapper.readValue(response.body().string(), new TypeReference<List<ProviderSubscription>>(){});
        }
    }

    public void unsubscribeProvider(Long subscriptionId) throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/api/subscriptions/providers/" + subscriptionId)
                .delete()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed: " + response);
        }
    }

    // ─── Notifications ────────────────────────────────────────────────────────

    public List<Notification> getNotifications(String userId, String userType) throws IOException {
        String url = baseUrl + "/api/notifications?userId=" + java.net.URLEncoder.encode(userId, "UTF-8")
                + "&userType=" + userType;
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed: " + response);
            return objectMapper.readValue(response.body().string(), new TypeReference<List<Notification>>(){});
        }
    }

    public void markNotificationRead(Long notificationId) throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/api/notifications/" + notificationId + "/read")
                .put(RequestBody.create("", MediaType.parse("application/json")))
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed: " + response);
        }
    }

    // ─── Montreal Open Data (Travaux) ─────────────────────────────────────────

    public List<MontrealAPIWork> getAllTravaux() throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/api/travaux")
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed: " + response);
            return objectMapper.readValue(response.body().string(),
                    new TypeReference<List<MontrealAPIWork>>() {});
        }
    }

    public List<MontrealAPIWork> filterTravaux(String filterKey, String filterValue) throws IOException {
        String url = baseUrl + "/api/travaux/filter?filterKey="
                + java.net.URLEncoder.encode(filterKey, "UTF-8")
                + "&filterValue=" + java.net.URLEncoder.encode(filterValue, "UTF-8");
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed: " + response);
            return objectMapper.readValue(response.body().string(),
                    new TypeReference<List<MontrealAPIWork>>() {});
        }
    }

    public void deleteAllTravaux() throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/api/travaux")
                .delete()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed: " + response);
        }
    }
}