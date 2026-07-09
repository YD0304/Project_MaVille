package ca.udem.maville.security;

import ca.udem.maville.model.Provider;
import ca.udem.maville.model.Resident;
import ca.udem.maville.repository.ProviderRepository;
import ca.udem.maville.repository.ResidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private ResidentRepository residentRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Try Resident
        var residentOpt = residentRepository.findByEmail(email);
        if (residentOpt.isPresent()) {
            Resident resident = residentOpt.get();
            return new User(
                resident.getEmail(),
                resident.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_RESIDENT"))
            );
        }

        // Try Provider
        var providerOpt = providerRepository.findByEmail(email);
        if (providerOpt.isPresent()) {
            Provider provider = providerOpt.get();
            return new User(
                provider.getEmail(),
                provider.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_PROVIDER"))
            );
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}