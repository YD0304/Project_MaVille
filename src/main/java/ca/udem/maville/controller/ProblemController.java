package ca.udem.maville.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.udem.maville.dto.ProblemRequestDTO;
import ca.udem.maville.dto.ProblemResponseDTO;
import ca.udem.maville.model.Priorite;
import ca.udem.maville.model.Problem;
import ca.udem.maville.model.Resident;
import ca.udem.maville.repository.ResidentRepository;
import ca.udem.maville.services.ServiceProblem;

@RestController
@RequestMapping("/api/problems")
public class ProblemController {

    private final ServiceProblem serviceProblem;
    private final ResidentRepository residentRepository;

    // FIX 1: ResidentRepository must be injected — it is required by reportProblem()
    //         to look up the Resident from the incoming residentId.
    public ProblemController(ServiceProblem serviceProblem,
                             ResidentRepository residentRepository) {
        this.serviceProblem = serviceProblem;
        this.residentRepository = residentRepository;
    }

    /**
     * POST /api/problems/report_problem
     * Body: { "residentId": 1, "street": "...", "neighbourhood": "...",
     *         "type": "...", "description": "..." }
     *
     * FIX 2: Removed the old duplicate @PostMapping that accepted a raw Problem body.
     *        There can only be one handler per HTTP method + path.
     *        The DTO-based version is kept because it avoids exposing the full JPA
     *        entity over the wire and resolves the Resident safely on the server side.
     *
     * Resident role: report a problem in their neighbourhood.
     */
    @PostMapping("/report_problem")
    public ResponseEntity<ProblemResponseDTO> reportProblem(@RequestBody ProblemRequestDTO req) {
        Resident resident = residentRepository.findById(req.getResidentId()).orElse(null);
        if (resident == null) {
            return ResponseEntity.badRequest().build();
        }

        Problem saved = serviceProblem.reportProblem(
                resident,
                req.getNeighbourhood(),
                req.getStreet(),
                req.getType(),
                req.getDescription()
        );
        return ResponseEntity.ok(ProblemResponseDTO.from(saved));
    }

    /**
     * GET /api/problems/my_reported_problems?residentId=1
     *
     * Resident role: view problems they personally reported.
     */
    @GetMapping("/my_reported_problems")
    public ResponseEntity<List<Problem>> viewMyProblems(@RequestParam Long residentId) {
        return ResponseEntity.ok(serviceProblem.viewMyProblems(residentId));
    }

    /**
     * GET /api/problems/all_reported_problems
     *
     * Agent / admin role: see every problem signal in real time.
     */
    @GetMapping("/all_reported_problems")
    public ResponseEntity<List<Problem>> viewAllProblems() {
        return ResponseEntity.ok(serviceProblem.viewAllProblems());
    }

    /**
     * GET /api/problems/problems_not_assigned
     *
     * Agent role: see raw signals not yet turned into a problem sheet.
     */
    @GetMapping("/problems_not_assigned")
    public ResponseEntity<List<Problem>> viewProblemsNotAssigned() {
        return ResponseEntity.ok(serviceProblem.viewProblemsNotAssigned());
    }

    /**
     * GET /api/problems/problems_assigned
     *
     * Agent / provider role: see problem sheets that have a priority assigned.
     */
    @GetMapping("/problems_assigned")
    public ResponseEntity<List<Problem>> viewProblemsAssigned() {
        return ResponseEntity.ok(serviceProblem.viewProblemsAssigned());
    }

    /**
     * POST /api/problems/link_signal?signalId=1&parentProblemId=2
     *
     * Agent role: link a raw signal to an existing problem sheet.
     * Returns 404 if either ID is invalid or the signal already has a priority.
     */
    @PostMapping("/link_signal")
    public ResponseEntity<Problem> linkSignalToProblem(
            @RequestParam Long signalId,
            @RequestParam Long parentProblemId) {

        Problem result = serviceProblem.linkSignalToProblem(signalId, parentProblemId);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/problems/assign_problem_priority?problemId=1&priorite=HIGH
     *
     * Agent role: assign a priority to a raw signal, creating a problem sheet.
     * Returns 404 if the problem doesn't exist or has already been processed.
     */
    @PostMapping("/assign_problem_priority")
    public ResponseEntity<Problem> assignProblemPriority(
            @RequestParam Long problemId,
            @RequestParam Priorite priorite) {

        Problem result = serviceProblem.assignProblemPriority(problemId, priorite);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }
}