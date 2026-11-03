package ca.udem.maville.security;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ResidentRepository residentRepository;
    private final ProviderRepository providerRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          ResidentRepository residentRepository,
                          ProviderRepository providerRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.residentRepository = residentRepository;
        this.providerRepository = providerRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(401).body(Map.of("authenticated", false));
        }

        String email = auth.getName();

        Optional<Resident> residentOpt = residentRepository.findByEmail(email);
        if (residentOpt.isPresent()) {
            Resident r = residentOpt.get();
            String fullName = (r.getFirst_name() + " " + r.getLast_name()).trim();
            if (fullName.isEmpty()) fullName = email;
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

        return ResponseEntity.status(500).body(Map.of("error", "User not found in database"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String role;
        String userId;

        Optional<Resident> residentOpt = residentRepository.findByEmail(request.getEmail());
        if (residentOpt.isPresent()) {
            role   = "RESIDENT";
            userId = residentOpt.get().getId().toString();
        } else {
            Optional<Provider> providerOpt = providerRepository.findByEmail(request.getEmail());
            if (providerOpt.isPresent()) {
                role   = "PROVIDER";
                userId = providerOpt.get().getCompanyNumber();
            } else {
                return ResponseEntity.status(500).body("Authenticated user not found in database");
            }
        }

        String token = jwtUtil.generateToken(request.getEmail(), role, userId);
        return ResponseEntity.ok(new JwtAuthResponse(token, request.getEmail(), role, userId));
    }
}
