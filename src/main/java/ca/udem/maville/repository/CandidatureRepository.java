// package ca.udem.maville.repository;

// import ca.udem.maville.model.Candidature;
// import ca.udem.maville.model.StatutProjet;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.jpa.repository.EntityGraph;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.lang.NonNull;
// import org.springframework.stereotype.Repository;

// import java.util.List;

// @Repository
// public interface CandidatureRepository extends JpaRepository<Candidature, Integer> {

//     @Override
//     @EntityGraph(attributePaths = {"prestataire", "problem"})
//     @NonNull
//     Page<Candidature> findAll(@NonNull Pageable pageable);

//     @EntityGraph(attributePaths = {"prestataire", "problem"})
//     List<Candidature> findByStatus(StatutProjet status);

//     @EntityGraph(attributePaths = {"prestataire", "problem"})
//     @Query("SELECT c FROM Candidature c WHERE c.prestataire.companyNumber = :neq")
//     List<Candidature> findByPrestataireCompanyNumber(@Param("neq") String neq);

//     @EntityGraph(attributePaths = {"prestataire", "problem"})
//     Page<Candidature> findByStatus(StatutProjet status, Pageable pageable);
// }