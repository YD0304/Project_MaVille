/*package ca.udem.maville.services;

import java.util.List;

import org.springframework.stereotype.Service;

import ca.udem.maville.model.User;
import ca.udem.maville.repository.UserRepository;

@Service
public class ServiceUser {
    private UserRepository userRepository;

    public ServiceUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUserRepository() {
        return userRepository.getAllUsers();
    }

    public User authenticate(String username, String password) {
        System.out.println("=== DEBUG: Authentication Attempt ===");
        System.out.println("Input username: '" + username + "'");
        System.out.println("Input password: '" + password + "'");
        
        List<User> allUsers = userRepository.getAllUsers();
        System.out.println("Total users in repository: " + allUsers.size());
        
        for (User user : allUsers) {
            System.out.println("Checking user: " + user.getUsername());
            System.out.println("Stored password: '" + user.getPassword() + "'");
            System.out.println("Password match: " + user.getPassword().equals(password));
            
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                System.out.println("=== DEBUG: Authentication SUCCESS ===");
                return user;
            }
        }
        System.out.println("=== DEBUG: Authentication FAILED ===");
        return null;
    }

    public void register(User user) {
        userRepository.addUser(user);
    }
}   */