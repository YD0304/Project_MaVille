package ca.udem.maville.security;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.udem.maville.model.Provider;
import ca.udem.maville.model.Resident;
import ca.udem.maville.repository.ProviderRepository;
import ca.udem.maville.repository.ResidentRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;   // ← single consolidated JWT utility

    @Autowired
    private ResidentRepository residentRepository;

    @Autowired
    private ProviderRepository providerRepository;

    /**
     * GET /api/auth/me
     * Returns detailed info about the currently authenticated user.
     * Replaces the old UserController's /api/users/me.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(401).body(Map.of("authenticated", false));
        }

        String email = auth.getName();

        // Try Resident
        Optional<Resident> residentOpt = residentRepository.findByEmail(email);
        if (residentOpt.isPresent()) {
            Resident r = residentOpt.get();
            String fullName = (r.getFirst_name() + " " + r.getLast_name()).trim();
            if (fullName.isEmpty()) fullName = email; // fallback to email
            return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "id", r.getId(),
                "email", email,
                "role", "RESIDENT",
                "name", fullName,
                "neighbourhood", r.getNeighbourhood() != null ? r.getNeighbourhood() : "",
                "authorities", auth.getAuthorities().stream()
                    .map(g -> g.getAuthority())
                    .collect(Collectors.toList())
            ));
        }

        // Try Provider
        Optional<Provider> providerOpt = providerRepository.findByEmail(email);
        if (providerOpt.isPresent()) {
            Provider p = providerOpt.get();
            String displayName = p.getCompanyName() != null ? p.getCompanyName() : email;
            return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "id", p.getCompanyNumber(),
                "email", email,
                "role", "PROVIDER",
                "name", displayName,
                "authorities", auth.getAuthorities().stream()
                    .map(g -> g.getAuthority())
                    .collect(Collectors.toList())
            ));
        }

        // Should never happen because authentication succeeded
        return ResponseEntity.status(500).body(Map.of("error", "User not found in database"));
    }






    /**
     * POST /api/auth/login
     * Authenticates credentials, looks up the user's role, and returns a JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // 1. Validate credentials via Spring Security (uses BCrypt via DaoAuthenticationProvider)
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2. Resolve role and userId from the database
        String role;
        String userId;

        if (residentRepository.findByEmail(request.getEmail()).isPresent()) {
            Resident resident = residentRepository.findByEmail(request.getEmail()).get();
            role   = "RESIDENT";
            userId = resident.getId().toString();
        } else if (providerRepository.findByEmail(request.getEmail()).isPresent()) {
            Provider provider = providerRepository.findByEmail(request.getEmail()).get();
            role   = "PROVIDER";
            userId = provider.getCompanyNumber();
        } else {
            // AuthenticationManager already verified credentials, so this is a data inconsistency
            return ResponseEntity.status(500).body("Authenticated user not found in database");
        }

        // 3. Issue JWT with role and userId embedded as claims
        String token = jwtUtil.generateToken(request.getEmail(), role, userId);

        // 4. Return token + metadata to the client
        return ResponseEntity.ok(new JwtAuthResponse(token, request.getEmail(), role, userId));
    }
}