package ca.udem.maville.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.udem.maville.model.Resident;
import ca.udem.maville.repository.ResidentRepository;

@RestController
@RequestMapping("/api/resident")
public class ResidentController {
 @Autowired
    private ResidentRepository residentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "Resident dashboard";
    }

   
    @PostMapping("/register")
    public String registerResident(@RequestBody Resident resident) {
        resident.setPassword(passwordEncoder.encode(resident.getPassword()));
        resident.setRole("RESIDENT");
        residentRepository.save(resident);
        return "Resident registered";
    }
}