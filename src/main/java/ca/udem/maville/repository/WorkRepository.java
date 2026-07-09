// package ca.udem.maville.repository;

// import java.time.LocalDate;
// import java.util.List;

// import org.springframework.data.jpa.repository.EntityGraph;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

// import ca.udem.maville.model.StatutProjet;
// import ca.udem.maville.model.Work;

// /**
//  * Repository pour les projets
//  * Optimisé avec @EntityGraph pour éviter les N+1 queries
//  */
// // ca.udem.maville.repository.WorkRepository
// @Repository
// public interface WorkRepository extends JpaRepository<Work, Long> {

//     @Query("SELECT p FROM Work p WHERE p.provider.companyNumber = :neq")
//     List<Work> findByProviderNeq(@Param("neq") String neq);

//     @EntityGraph(attributePaths = {"provider", "problems"})
//     List<Work> findByStatus(StatutProjet status);

//     @EntityGraph(attributePaths = {"provider", "problems"})
//     @Query("SELECT DISTINCT p FROM Work p JOIN p.problems pr WHERE pr.id = :problemeId")
//     List<Work> findByProblemeId(@Param("problemeId") Long problemeId);

//     // --- Nouvelles méthodes ---
//     List<Work> findByStartDateBetween(LocalDate start, LocalDate end);

//     List<Work> findByCategory(String category);

//     List<Work> findByNeighbourhood(String neighbourhood);

//     List<Work> findByStreet(String street);

//     List<Work> findByProviderCompanyName(String companyName);
// }

