package ca.udem.maville.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ca.udem.maville.model.Provider;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, String> {
    Optional<Provider> findByEmail(String email);
    //boolean existsByEmail(String email);
   Optional<Provider> findByCompanyNumber(String companyNumber);

}
