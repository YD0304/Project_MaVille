package ca.udem.maville.security;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.udem.maville.model.Provider;
import ca.udem.maville.model.Resident;
import ca.udem.maville.repository.ProviderRepository;
import ca.udem.maville.repository.ResidentRepository;


@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired private ResidentRepository residentRepository;
    @Autowired private ProviderRepository providerRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication auth) {
        String username = auth.getName(); // email or companyNumber
        Optional<Resident> resident = residentRepository.findByEmail(username);
        if (resident.isPresent()) {
            return ResponseEntity.ok(Map.of("id", resident.get().getId(), "role", "RESIDENT", "email", username));
        }
        Optional<Provider> provider = providerRepository.findByEmail(username);
        if (provider.isPresent()) {
            return ResponseEntity.ok(Map.of("id", provider.get().getCompanyNumber(), "role", "PROVIDER", "companyNumber", username));
        }
        return ResponseEntity.notFound().build();
    }

    // @PutMapping("/me")
    // public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> updates, Authentication auth) {
    //     String username = auth.getName();
    //     Optional<Resident> residentOpt = residentRepository.findByEmail(username);
    //     if (residentOpt.isPresent()) {
    //         Resident resident = residentOpt.get();
    //         if (updates.containsKey("LastName")) resident.setLast_name((String) updates.get("LastName"));
    //         if (updates.containsKey("FirstName")) resident.setFirst_name((String) updates.get("FirstName"));
    //         if (updates.containsKey("Neighbourhood")) resident.setNeighbourhood((String) updates.get("Neighbourhood"));
    //         // Add more fields as needed
    //         residentRepository.save(resident);
    //         return ResponseEntity.ok(Map.of("id", resident.getId(), "role", "RESIDENT", "email", username));
    //     }
    //     Optional<Provider> providerOpt = providerRepository.findByCompanyNumber(username);
    //     if (providerOpt.isPresent()) {
    //         Provider provider = providerOpt.get();
    //         if (updates.containsKey("name")) provider.setCompanyName((String) updates.get("name"));
    //         if (updates.containsKey("address")) provider.((String) updates.get("address"));
    //         // Add more fields as needed
    //         providerRepository.save(provider);
    //         return ResponseEntity.ok(Map.of("id", provider.getCompanyNumber(), "role", "PROVIDER", "companyNumber", username));
    //     }
    //     return ResponseEntity.notFound().build();
    }