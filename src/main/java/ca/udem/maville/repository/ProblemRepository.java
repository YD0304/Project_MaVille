package ca.udem.maville.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ca.udem.maville.model.Priorite;
import ca.udem.maville.model.Problem;
import ca.udem.maville.model.WorkType;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {

    // Used by ServiceProblem.viewMyProblems()
    List<Problem> findByResidentId(Long residentId);

    // Used by ServiceProblem.viewProblemsNotAssigned()
    List<Problem> findByPrioriteType(Priorite prioriteType);

    // Used by ServiceProblem.viewProblemsAssigned()
    //   → all problems that are neither NOT_ASSIGNED nor REFUSED
    List<Problem> findByPrioriteTypeNotAndPrioriteTypeNot(Priorite p1, Priorite p2);

    // Optional filters used by admin / provider views
    @EntityGraph(attributePaths = {"resident"})
    @Query("SELECT p FROM Problem p WHERE p.prioriteType != ca.udem.maville.model.Priorite.NOT_ASSIGNED" +
           " AND (:type IS NULL OR p.type = :type)")
    List<Problem> findNonAssignedWithFilters(@Param("type") WorkType type);

    @EntityGraph(attributePaths = {"resident"})
    List<Problem> findByType(WorkType type);

    @EntityGraph(attributePaths = {"resident"})
    List<Problem> findByNeighbourhood(String neighbourhood);

    @EntityGraph(attributePaths = {"resident"})
    @Query("SELECT p FROM Problem p WHERE p.resident.email = :email")
    List<Problem> findByResidentEmail(@Param("email") String email);


}