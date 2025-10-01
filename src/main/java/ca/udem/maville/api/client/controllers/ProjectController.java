package ca.udem.maville.api.client.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.udem.maville.enums.StatutProjet;
import ca.udem.maville.logic.ServiceProject;
import ca.udem.maville.model.Candidature;
import ca.udem.maville.model.Prestataire;
import ca.udem.maville.model.Work;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ServiceProject serviceProject;

    public ProjectController(ServiceProject serviceProject) {
        this.serviceProject = serviceProject;
    }

    // ---- Proposals ----

    @GetMapping("/proposals")
    public ResponseEntity<List<Candidature>> viewAllProposals() {
        return ResponseEntity.ok(serviceProject.viewAllProposals());
    }
    @PostMapping("/{problemId}/proposals")
    public ResponseEntity<Candidature> submitProposal(
            @PathVariable int problemId,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam double cost,
            @RequestBody Prestataire prestataire) {

        Candidature proposal = serviceProject.submitProposalForProblem(
                problemId, title, description, startDate, endDate, cost, prestataire);

        return (proposal != null) ? ResponseEntity.ok(proposal) : ResponseEntity.badRequest().build();
    }

    @GetMapping("/my-proposals")
    public ResponseEntity<List<Candidature>> viewMyProposals(@RequestBody Prestataire prestataire) {
        return ResponseEntity.ok(serviceProject.viewMyProposals(prestataire));
    }

    // ---- Work Updates ----

    @PutMapping("/{projectId}/update_status")
    public ResponseEntity<Work> updateWorkStatus(
            @PathVariable int projectId,
            @RequestParam StatutProjet status,
            @RequestBody Prestataire prestataire) {

        Work updated = serviceProject.updateWorkStatus(projectId, prestataire, status);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.badRequest().build();
    }

    @PutMapping("/{projectId}/update_description")
    public ResponseEntity<Work> updateWorkDescription(
            @PathVariable int projectId,
            @RequestParam String description,
            @RequestBody Prestataire prestataire) {

        Work updated = serviceProject.updateWorkDescription(projectId, prestataire, description);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.badRequest().build();
    }

    @PutMapping("/{projectId}/update_end-date")
    public ResponseEntity<Work> updateWorkEndDate(
            @PathVariable int projectId,
            @RequestParam String newDate,
            @RequestBody Prestataire prestataire) {

        Work updated = serviceProject.updateWorktEndDate(projectId, prestataire, newDate);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.badRequest().build();
    }

    // ---- STPM ----

    @GetMapping("/proposals/submitted")
    public ResponseEntity<List<Candidature>> getSubmittedProposals() {
        return ResponseEntity.ok(serviceProject.proposalsSubmited());
    }

    @PostMapping("/proposals/{projectId}/evaluate")
    public ResponseEntity<String> evaluateProposal(
            @PathVariable int projectId,
            @RequestParam StatutProjet status) {
        Work result = serviceProject.proposalEvaluation(projectId, status);
        return ResponseEntity.ok("Proposal evaluation " + ((result != null) ? "successful" : "failed"));
    }
}
