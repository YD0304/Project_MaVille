package ca.udem.maville.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.udem.maville.model.User;

import org.springframework.stereotype.Repository;
@Repository
public class UserRepository {
    private static final String JSON_FILE = "users.json";
    private ObjectMapper objectMapper;
    private List<User> users;

    public UserRepository() {
        this.objectMapper = new ObjectMapper();
        this.users = new ArrayList<>();
        loadUsers();
    }

    // Load users from JSON file
    public void loadUsers() {
        try {
            File file = new File(JSON_FILE);
            if (file.exists() && file.length() > 0) {
                // Use TypeReference for better type handling
                TypeReference<List<User>> typeRef = new TypeReference<List<User>>() {};
                users = objectMapper.readValue(file, typeRef);
            } else {
                users = new ArrayList<>();
            }
        } catch (IOException e) {
            System.err.println("Error loading users from JSON: " + e.getMessage());
            e.printStackTrace();
            users = new ArrayList<>();
        }
    }

    // Save users to JSON file
    private void saveUsers() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(JSON_FILE), users);
        } catch (IOException e) {
            System.err.println("Error saving users to JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Add a new user
    public void addUser(User user) {
        users.add(user);
        saveUsers();
    }

    // Find user by username
    public Optional<User> findByUsername(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    // Find all users by role
    public List<User> findByRole(String role) {
        return users.stream()
                .filter(user -> user.getRole().equals(role))
                .collect(Collectors.toList());
    }

    // Update a user
    public boolean updateUser(User updatedUser) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(updatedUser.getUsername())) {
                users.set(i, updatedUser);
                saveUsers();
                return true;
            }
        }
        return false;
    }

    // Delete a user by username
    public boolean deleteUser(String username) {
        boolean removed = users.removeIf(user -> user.getUsername().equals(username));
        if (removed) {
            saveUsers();
        }
        return removed;
    }


    
    // Get all users
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    // Verify user credentials
    public boolean verifyCredentials(String username, String password) {
        Optional<User> user = findByUsername(username);
        return user.isPresent() &&
               user.get().getPassword() != null &&
               user.get().getPassword().equals(password);
    }
}