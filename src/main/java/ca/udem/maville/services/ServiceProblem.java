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
public class ServiceProblem {

    private final ProblemRepository problemRepository;
    private final NotificationService notificationService;

    public ServiceProblem(ProblemRepository problemRepository, NotificationService notificationService) {
        this.problemRepository = problemRepository;
        this.notificationService = notificationService;
    }

    // ---------- Resident actions ----------

    public Problem reportProblem(Resident resident, String neighbourhood,
                                 String street, WorkType type, String description) {
        Problem newProblem = new Problem(street, neighbourhood, type, description, resident);
        newProblem.setPrioriteType(Priorite.NOT_ASSIGNED);

            notificationService.notifyProvidersAboutNewProblem(newProblem);
            notificationService.notifySTPM("New problem reported: " + newProblem.getDescription(), newProblem.getId());


        return problemRepository.save(newProblem);
    }

    /**
     * Returns all problems reported by a given resident (looked up by their DB id).
     */
    public List<Problem> viewMyProblems(Long residentId) {
        return problemRepository.findByResidentId(residentId);
    }

    // ---------- Admin / STPM agent actions ----------

    public List<Problem> viewAllProblems() {
        return problemRepository.findAll();
    }

    /** Raw signals not yet turned into a problem sheet (no priority assigned). */
    public List<Problem> viewProblemsNotAssigned() {
        return problemRepository.findByPrioriteType(Priorite.NOT_ASSIGNED);
    }

    /** Problem sheets that have a priority (i.e. have been processed by an agent). */
    public List<Problem> viewProblemsAssigned() {
        return problemRepository.findByPrioriteTypeNotAndPrioriteTypeNot(
                Priorite.NOT_ASSIGNED, Priorite.REFUSED);
    }

    /**
     * Agent links a raw signal to an existing problem sheet. The signal's priority
     * is updated to match the parent's priority. Returns null if either ID is
     * invalid or the signal has already been processed.
     */
    public Problem linkSignalToProblem(Long signalId, Long parentProblemId) {
        Problem signal = problemRepository.findById(signalId).orElse(null);
        Problem parent = problemRepository.findById(parentProblemId).orElse(null);
        if (signal == null || parent == null) {
            return null;
        }
        if (signal.getPrioriteType() != Priorite.NOT_ASSIGNED) {
            return null;
        }
        if (parent.getPrioriteType() == Priorite.NOT_ASSIGNED) {
            // Target must already be a fiche problème (has a priority)
            return null;
        }
        signal.setParentProblem(parent);
        signal.setPrioriteType(parent.getPrioriteType());
        return problemRepository.save(signal);
    }

    /**
     * Agent assigns a priority to a raw signal, turning it into a problem sheet.
     * Returns null if the problem does not exist or has already been processed.
     */
    public Problem assignProblemPriority(Long problemId, Priorite priorite) {
        Problem problem = problemRepository.findById(problemId).orElse(null);
        if (problem == null) {
            return null;
        }
        if (problem.getPrioriteType() != Priorite.NOT_ASSIGNED) {
            // Already assigned or refused – do not override
            return null;
        }
        problem.setPrioriteType(priorite);
        return problemRepository.save(problem);
    }
}