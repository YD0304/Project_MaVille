package ca.udem.maville.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.udem.maville.model.Priorite;
import ca.udem.maville.model.Problem;
import ca.udem.maville.model.Resident;
import ca.udem.maville.model.WorkType;
import ca.udem.maville.repository.ProblemRepository;

@Service
@Transactional
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final NotificationService notificationService;

    public ProblemService(ProblemRepository problemRepository, NotificationService notificationService) {
        this.problemRepository = problemRepository;
        this.notificationService = notificationService;
    }

    public Problem reportProblem(Resident resident, String neighbourhood,
                                 String street, WorkType type, String description) {
        Problem problem = new Problem(street, neighbourhood, type, description, resident);
        problem.setPrioriteType(Priorite.NOT_ASSIGNED);
        // Save first so problem.getId() is populated before notifications
        Problem saved = problemRepository.save(problem);
        notificationService.notifyProvidersAboutNewProblem(saved);
        notificationService.notifySTPM("New problem reported: " + saved.getDescription(), saved.getId());
        return saved;
    }

    public List<Problem> viewMyProblems(Long residentId) {
        return problemRepository.findByResidentId(residentId);
    }

    public List<Problem> viewAllProblems() {
        return problemRepository.findAll();
    }

    public List<Problem> viewProblemsNotAssigned() {
        return problemRepository.findByPrioriteType(Priorite.NOT_ASSIGNED);
    }

    public List<Problem> viewProblemsAssigned() {
        return problemRepository.findByPrioriteTypeNotAndPrioriteTypeNot(
                Priorite.NOT_ASSIGNED, Priorite.REFUSED);
    }

    /**
     * Links a raw signal to an existing problem sheet. The signal's priority
     * is updated to match the parent's. Returns null if either ID is invalid,
     * the signal already has a priority, or the parent is itself unprocessed.
     */
    public Problem linkSignalToProblem(Long signalId, Long parentProblemId) {
        Problem signal = problemRepository.findById(signalId).orElse(null);
        Problem parent = problemRepository.findById(parentProblemId).orElse(null);
        if (signal == null || parent == null) return null;
        if (signal.getPrioriteType() != Priorite.NOT_ASSIGNED) return null;
        if (parent.getPrioriteType() == Priorite.NOT_ASSIGNED) return null;
        signal.setParentProblem(parent);
        signal.setPrioriteType(parent.getPrioriteType());
        return problemRepository.save(signal);
    }

    /**
     * Assigns a priority to a raw signal, turning it into a problem sheet.
     * Returns null if the problem does not exist or has already been processed.
     */
    public Problem assignProblemPriority(Long problemId, Priorite priorite) {
        Problem problem = problemRepository.findById(problemId).orElse(null);
        if (problem == null) return null;
        if (problem.getPrioriteType() != Priorite.NOT_ASSIGNED) return null;
        problem.setPrioriteType(priorite);
        return problemRepository.save(problem);
    }
}
