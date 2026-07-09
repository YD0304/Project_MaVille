package ca.udem.maville.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.udem.maville.model.Priorite;
import ca.udem.maville.model.Problem;
import ca.udem.maville.model.Project;
import ca.udem.maville.model.ProjectStatus;
import ca.udem.maville.model.Provider;
import ca.udem.maville.model.Resident;
import ca.udem.maville.model.WorkType;
import ca.udem.maville.repository.ProblemRepository;
import ca.udem.maville.repository.ProjectRepository;
import ca.udem.maville.repository.ProviderRepository;
import ca.udem.maville.repository.ResidentRepository;
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProblemRepository problemRepository;
    private final ProviderRepository providerRepository;
    private final ResidentRepository residentRepository;
    private final NotificationService notificationService;

    public ProjectService(ProjectRepository projectRepository,
                          ProblemRepository problemRepository,
                          ProviderRepository providerRepository,
                          ResidentRepository residentRepository,
                          NotificationService notificationService) {
        this.projectRepository = projectRepository;
        this.problemRepository = problemRepository;
        this.providerRepository = providerRepository;
        this.residentRepository = residentRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public Project submitProposal(Long problemId, String title, String description,
                                  double proposedCost, LocalDate proposedStartDate,
                                  LocalDate proposedEndDate, String providerCompanyNumber) {
        Optional<Problem> problemOpt = problemRepository.findById(problemId);
        Optional<Provider> providerOpt = providerRepository.findById(providerCompanyNumber);
        if (problemOpt.isEmpty() || providerOpt.isEmpty()) return null;
        Problem problem = problemOpt.get();
        if (problem.getPrioriteType() == Priorite.NOT_ASSIGNED) return null;

        Project project = new Project(title, description, proposedCost,
                proposedStartDate, proposedEndDate, problem, providerOpt.get());
        notificationService.notifyResidentsAboutProject(project, "CREATED");
        notificationService.notifySTPM("New proposal submitted for problem " + problemId, project.getId());
        
        return projectRepository.save(project);
    }

    public List<Project> getMyProposals(String providerCompanyNumber) {
        return projectRepository.findByProvider_CompanyNumber(providerCompanyNumber);
    }

    @Transactional
    public Project updateProposalDescription(Long projectId, String providerCompanyNumber, String newDescription) {
        Project project = findAndCheckOwnership(projectId, providerCompanyNumber);
        if (project.getStatus() != ProjectStatus.PROPOSAL_SUBMITTED)
            throw new IllegalStateException("Can only modify submitted proposals");
        project.setDescription(newDescription);
        return projectRepository.save(project);
    }

    @Transactional
    public Project updateProposalEndDate(Long projectId, String providerCompanyNumber, LocalDate newEndDate) {
        Project project = findAndCheckOwnership(projectId, providerCompanyNumber);
        if (project.getStatus() != ProjectStatus.PROPOSAL_SUBMITTED)
            throw new IllegalStateException("Can only modify submitted proposals");
        project.setProposedEndDate(newEndDate);
        return projectRepository.save(project);
    }

    public List<Project> getSubmittedProposals() {
        return projectRepository.findByStatus(ProjectStatus.PROPOSAL_SUBMITTED);
    }

    @Transactional
    public Project acceptProposal(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        project.accept();// change status to PERMIT_ISSUED

        notificationService.notifyProviderAboutDecision(project);
        notificationService.notifyResidentsAboutProject(project, "STATUS_CHANGED");

        return projectRepository.save(project);
    }

    @Transactional
    public Project rejectProposal(Long projectId, String reason) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        project.reject(reason);
        notificationService.notifyProviderAboutDecision(project);
        return projectRepository.save(project);
    }
 
    @Transactional
    public Project startWork(Long projectId, String providerCompanyNumber) {
        Project project = findAndCheckOwnership(projectId, providerCompanyNumber);
        project.startWork(); // change status to IN_PROGRESS
        notificationService.notifyResidentsAboutProject(project, "STATUS_CHANGED");
        return projectRepository.save(project);
    }

    @Transactional
    public Project delayWork(Long projectId, String providerCompanyNumber) {
        Project project = findAndCheckOwnership(projectId, providerCompanyNumber);
        project.delay();
        notificationService.notifyResidentsAboutProject(project, "STATUS_CHANGED");
        return projectRepository.save(project);
    }

    @Transactional
    public Project resumeWork(Long projectId, String providerCompanyNumber) {
        Project project = findAndCheckOwnership(projectId, providerCompanyNumber);
        project.resume();
        notificationService.notifyResidentsAboutProject(project, "STATUS_CHANGED");
        return projectRepository.save(project);
    }

    @Transactional
    public Project completeWork(Long projectId, String providerCompanyNumber, Double actualCost) {
        Project project = findAndCheckOwnership(projectId, providerCompanyNumber);
        project.complete(actualCost);   // ✅ passes actualCost to entity
        notificationService.notifyResidentsAboutProject(project, "STATUS_CHANGED");

        return projectRepository.save(project);
    }

    @Transactional
    public void reportProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        project.incrementReportedCount();
        notificationService.notifySTPM("Project reported: " + project.getId(), project.getId());
        projectRepository.save(project);
    }

    private Project findAndCheckOwnership(Long projectId, String providerCompanyNumber) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        if (!project.getProvider().getCompanyNumber().equals(providerCompanyNumber))
            throw new SecurityException("Provider does not own this project");
        return project;
    }

    // Add these methods to ProjectService

public List<Project> getProjectsByStatus(ProjectStatus status) {
    return projectRepository.findByStatus(status);
}

public List<Project> getProjectsByDateRange(LocalDate start, LocalDate end) {
    return projectRepository.findByProposedStartDateBetween(start, end);
}

public List<Project> getProjectsByNeighbourhood(String neighbourhood) {
    return projectRepository.findByProblem_Neighbourhood(neighbourhood);
}

public List<Project> getProjectsByStreet(String street) {
    return projectRepository.findByProblem_Street(street);
}

public List<Project> getProjectsByType(WorkType type) {
    return projectRepository.findByProblem_Type(type);
}

public List<Project> getProjectsByPriority(String priority) {
    return projectRepository.findByProblem_PrioriteType(Priorite.valueOf(priority));
}

public List<Project> getAllProjects() {
    return projectRepository.findAll();
}

public List<Project> getMyProjects(String email) {
    var residentOpt = residentRepository.findByEmail(email);
    if (residentOpt.isPresent()) {
        Resident resident = residentOpt.get();
        return projectRepository.findByProblem_Neighbourhood(resident.getNeighbourhood());
    }
    var providerOpt = providerRepository.findByEmail(email);
    if (providerOpt.isPresent()) {
        Provider provider = providerOpt.get();
        return projectRepository.findByProvider_CompanyNumber(provider.getCompanyNumber());
    }
    return List.of();
}

public List<Project> filterProjects(String neighbourhood, String street,
                                        WorkType type, Priorite priority,
                                        ProjectStatus status,
                                        LocalDate startDate, LocalDate endDate) {
        return projectRepository.filterProjects(
            neighbourhood, street, type, priority, status, startDate, endDate
        );
    }
}