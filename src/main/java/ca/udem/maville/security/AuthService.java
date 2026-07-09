package ca.udem.maville.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ca.udem.maville.model.Provider;
import ca.udem.maville.model.Resident;
import ca.udem.maville.repository.ProviderRepository;
import ca.udem.maville.repository.ResidentRepository;

/**
 * AuthService is an optional helper layer.
 * The primary login flow lives in AuthController (uses AuthenticationManager + JwtUtil).
 * This service can be used by other internal components that need to programmatically
 * validate credentials without going through the HTTP layer.
 */
@Service
public class AuthService {

    @Autowired
    private ResidentRepository residentRepo;

    @Autowired
    private ProviderRepository providerRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;   // ← BCrypt from SecurityConfig

    /**
     * Validate raw credentials and return a LoginResponse (token + role).
     * Throws RuntimeException on failure — callers should catch and map to HTTP status.
     */
    public LoginResponse login(String email, String password) {
        // Try resident
        var residentOpt = residentRepo.findByEmail(email);
        if (residentOpt.isPresent()) {
            Resident r = residentOpt.get();
            if (passwordEncoder.matches(password, r.getPassword())) {
                String token = jwtUtil.generateToken(email, "RESIDENT", r.getId().toString());
                return new LoginResponse(token, "RESIDENT");
            }
        }

        // Try provider
        var providerOpt = providerRepo.findByEmail(email);
        if (providerOpt.isPresent()) {
            Provider p = providerOpt.get();
            if (passwordEncoder.matches(password, p.getPassword())) {
                String token = jwtUtil.generateToken(email, "PROVIDER", p.getCompanyNumber());
                return new LoginResponse(token, "PROVIDER");
            }
        }

        throw new RuntimeException("Invalid credentials");
    }
}