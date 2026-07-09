package ca.udem.maville.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ca.udem.maville.model.Priorite;
import ca.udem.maville.model.Project;
import ca.udem.maville.model.ProjectStatus;
import ca.udem.maville.model.WorkType;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @EntityGraph(attributePaths = {"problem", "provider"})
    List<Project> findByStatus(ProjectStatus status);

    @EntityGraph(attributePaths = {"problem", "provider"})
    Page<Project> findByStatus(ProjectStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"problem", "provider"})
    List<Project> findByProvider_CompanyNumber(String companyNumber);

    @EntityGraph(attributePaths = {"problem", "provider"})
    List<Project> findByProblem_Id(Long problemId);

    // New methods for filtering projects
    @EntityGraph(attributePaths = {"problem", "provider"})
    List<Project> findByProposedStartDateBetween(LocalDate start, LocalDate end);

    @EntityGraph(attributePaths = {"problem", "provider"})
    List<Project> findByProblem_Neighbourhood(String neighbourhood);

    @EntityGraph(attributePaths = {"problem", "provider"})
    List<Project> findByProblem_Street(String street);

    @EntityGraph(attributePaths = {"problem", "provider"})
    List<Project> findByProblem_Type(WorkType type);

    @EntityGraph(attributePaths = {"problem", "provider"})
    List<Project> findByProblem_PrioriteType(Priorite priorite);

    // New: combined filter with optional parameters
    @Query("SELECT p FROM Project p WHERE " +
       "(:neighbourhood IS NULL OR p.problem.neighbourhood = :neighbourhood) AND " +
       "(:street IS NULL OR p.problem.street = :street) AND " +
       "(:type IS NULL OR p.problem.type = :type) AND " +
       "(:priority IS NULL OR p.problem.prioriteType = :priority) AND " +   // ← fixed
       "(:status IS NULL OR p.status = :status) AND " +
       "(:startDate IS NULL OR p.proposedStartDate >= :startDate) AND " +
       "(:endDate IS NULL OR p.proposedEndDate <= :endDate)")
    List<Project> filterProjects(@Param("neighbourhood") String neighbourhood,
                                 @Param("street") String street,
                                 @Param("type") WorkType type,
                                 @Param("priority") Priorite priority,
                                 @Param("status") ProjectStatus status,
                                 @Param("startDate") LocalDate startDate,
                                 @Param("endDate") LocalDate endDate);
}