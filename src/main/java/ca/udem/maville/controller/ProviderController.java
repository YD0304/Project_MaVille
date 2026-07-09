package ca.udem.maville.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.udem.maville.model.Provider;
import ca.udem.maville.repository.ProviderRepository;

@RestController
@RequestMapping("/api/provider")
public class ProviderController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProviderRepository providerRepository;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "Provider dashboard";
    }

    @PostMapping("/register")
    public String registerProvider(@RequestBody Provider provider) {
        provider.setPassword(passwordEncoder.encode(provider.getPassword()));
        provider.setRole("PROVIDER");
        providerRepository.save(provider);
        return "Provider registered";
    }
}