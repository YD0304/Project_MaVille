package ca.udem.maville.api.client.controllers;

import org.springframework.web.bind.annotation.PostMapping;

import ca.udem.maville.logic.ServiceProject;

public class ProjectController {
    private final ServiceProject serviceProject;
    public ProjectController(ServiceProject serviceProject) {
        this.serviceProject = serviceProject;
    }

    @PostMapping("/proposal_submission")
    public void proposalSubmission() {
        
        }

}
