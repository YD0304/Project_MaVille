package ca.udem.maville.logic;

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
        for (User user : userRepository.getAllUsers()) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public void register(User user) {
        userRepository.addUser(user);
    }
}