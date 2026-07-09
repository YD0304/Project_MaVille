package ca.udem.maville.repository;

import ca.udem.maville.model.Resident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour les résidents
 */
@Repository
public interface ResidentRepository extends JpaRepository<Resident, Long> { 
    // extend JpaRepository, Spring automatically creates:
//  save(resident)
// findById(id)
// findAll()
// deleteById(id)
    Optional<Resident> findByEmail(String email);
    //boolean existsByEmail(String email);
}

