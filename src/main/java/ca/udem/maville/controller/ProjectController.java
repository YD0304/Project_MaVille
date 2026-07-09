package ca.udem.maville.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import ca.udem.maville.model.Priorite;
import ca.udem.maville.model.Project;
import ca.udem.maville.model.ProjectStatus;
import ca.udem.maville.model.WorkType;
import ca.udem.maville.services.ProjectService;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // Provider endpoints
    @PostMapping("/submit")
    public ResponseEntity<Project> submitProposal(
            @RequestParam Long problemId,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam double proposedCost,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate proposedStartDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate proposedEndDate,
            @RequestParam String providerCompanyNumber) {

        Project project = projectService.submitProposal(problemId, title, description,
                proposedCost, proposedStartDate, proposedEndDate, providerCompanyNumber);
        return project != null ? ResponseEntity.ok(project) : ResponseEntity.badRequest().build();
    }

    @GetMapping("/my-proposals")
    public ResponseEntity<List<Project>> getMyProposals(@RequestParam String providerCompanyNumber) {
        return ResponseEntity.ok(projectService.getMyProposals(providerCompanyNumber));
    }

    @PutMapping("/{projectId}/description")
    public ResponseEntity<Project> updateDescription(
            @PathVariable Long projectId,
            @RequestParam String providerCompanyNumber,
            @RequestParam String newDescription) {
        return ResponseEntity.ok(projectService.updateProposalDescription(projectId, providerCompanyNumber, newDescription));
    }

    @PutMapping("/{projectId}/end-date")
    public ResponseEntity<Project> updateEndDate(
            @PathVariable Long projectId,
            @RequestParam String providerCompanyNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newEndDate) {
        return ResponseEntity.ok(projectService.updateProposalEndDate(projectId, providerCompanyNumber, newEndDate));
    }

    // Work progress for providers
    @PutMapping("/{projectId}/start")
    public ResponseEntity<Project> startWork(@PathVariable Long projectId, @RequestParam String providerCompanyNumber) {
        return ResponseEntity.ok(projectService.startWork(projectId, providerCompanyNumber));
    }

    @PutMapping("/{projectId}/delay")
    public ResponseEntity<Project> delayWork(@PathVariable Long projectId, @RequestParam String providerCompanyNumber) {
        return ResponseEntity.ok(projectService.delayWork(projectId, providerCompanyNumber));
    }

    @PutMapping("/{projectId}/resume")
    public ResponseEntity<Project> resumeWork(@PathVariable Long projectId, @RequestParam String providerCompanyNumber) {
        return ResponseEntity.ok(projectService.resumeWork(projectId, providerCompanyNumber));
    }

    @PutMapping("/{projectId}/complete")
    public ResponseEntity<Project> completeWork(
            @PathVariable Long projectId,
            @RequestParam String providerCompanyNumber,
            @RequestParam(required = false) Double actualCost) {
        return ResponseEntity.ok(projectService.completeWork(projectId, providerCompanyNumber, actualCost));
    }

    // STPM endpoints
    @GetMapping("/submitted")
    public ResponseEntity<List<Project>> getSubmittedProposals() {
        return ResponseEntity.ok(projectService.getSubmittedProposals());
    }

    @PostMapping("/{projectId}/accept")
    public ResponseEntity<Project> acceptProposal(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.acceptProposal(projectId));
    }

    @PostMapping("/{projectId}/reject")
    public ResponseEntity<Project> rejectProposal(@PathVariable Long projectId,
                                                   @RequestParam String reason) {
        return ResponseEntity.ok(projectService.rejectProposal(projectId, reason));
    }

    @PostMapping("/{projectId}/report")
    public ResponseEntity<Void> reportProject(@PathVariable Long projectId) {
        projectService.reportProject(projectId);
        return ResponseEntity.ok().build();
    }

    // Inside the new ProjectController (after the existing endpoints)

@GetMapping("/status")
public ResponseEntity<List<Project>> getProjectsByStatus(@RequestParam ProjectStatus status) {
    return ResponseEntity.ok(projectService.getProjectsByStatus(status));
}

@GetMapping("/date-range")
public ResponseEntity<List<Project>> getProjectsByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
    return ResponseEntity.ok(projectService.getProjectsByDateRange(start, end));
}

@GetMapping("/filter")
public ResponseEntity<List<Project>> filterProjects(
        @RequestParam(required = false) String neighbourhood,
        @RequestParam(required = false) String street,
        @RequestParam(required = false) WorkType type,
        @RequestParam(required = false) Priorite priority,
        @RequestParam(required = false) ProjectStatus status,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

    List<Project> projects = projectService.filterProjects(
        neighbourhood, street, type, priority, status, startDate, endDate
    );
    return ResponseEntity.ok(projects);
}

@GetMapping("/my")
public ResponseEntity<List<Project>> getMyProjects(Authentication auth) {
    String email = auth.getName();
    return ResponseEntity.ok(projectService.getMyProjects(email));
}
}