package ca.udem.maville.security;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ca.udem.maville.model.Provider;
import ca.udem.maville.model.Resident;
import ca.udem.maville.repository.ProviderRepository;
import ca.udem.maville.repository.ResidentRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final ResidentRepository residentRepository;
    private final ProviderRepository providerRepository;

    public CustomUserDetailsService(ResidentRepository residentRepository,
                                    ProviderRepository providerRepository) {
        this.residentRepository = residentRepository;
        this.providerRepository = providerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        java.util.Optional<Resident> residentOpt = residentRepository.findByEmail(email);
        if (residentOpt.isPresent()) {
            Resident resident = residentOpt.get();
            return new User(
                resident.getEmail(),
                resident.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_RESIDENT"))
            );
        }

        java.util.Optional<Provider> providerOpt = providerRepository.findByEmail(email);
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
