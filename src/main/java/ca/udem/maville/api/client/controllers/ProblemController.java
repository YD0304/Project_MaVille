package ca.udem.maville.api.client.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.udem.maville.enums.Priorite;
import ca.udem.maville.logic.ServiceProblem;
import ca.udem.maville.model.Problem;
import ca.udem.maville.model.Resident;

@RestController
@RequestMapping("/api/problems")
public class ProblemController {
    private final ServiceProblem serviceProblem;
    public ProblemController(ServiceProblem serviceProblem) {
        this.serviceProblem = serviceProblem;
    }

    @PostMapping("/report_problem")
    public void reportProblem() {
        }

    @GetMapping("/my_reported_problems")
    public ResponseEntity<List<Problem>> viewMyProblems(@RequestParam Resident resident) {
        return ResponseEntity.ok(serviceProblem.viewMyProblems(resident));

        }

    @GetMapping("/all_reported_problems")
    public ResponseEntity<List<Problem>> viewAllProblems() {
        return ResponseEntity.ok(serviceProblem.viewAllProblems());
        
        }
    @GetMapping("/problems_not_assigned")
    public ResponseEntity<List<Problem>>  ViewproblemsNotAssigned() {
        return ResponseEntity.ok(serviceProblem.ViewproblemsNotAssigned());
        }

    @PostMapping("/assign_problem_priority")
    public ResponseEntity<Problem> AssignProblemsPriority(@RequestParam int problemId, @RequestParam Priorite priorite) {
        return ResponseEntity.ok(serviceProblem.assignProblemPriority(problemId, priorite));
        }
}
