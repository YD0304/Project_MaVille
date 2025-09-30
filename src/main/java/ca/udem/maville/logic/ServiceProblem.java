package ca.udem.maville.logic;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ca.udem.maville.enums.Priorite; // Verify that the Problem class exists in this package or update the path
import ca.udem.maville.enums.TravauxType;
import ca.udem.maville.model.Problem;
import ca.udem.maville.model.Resident;
import ca.udem.maville.repository.ProblemRepository;

@Service
public class ServiceProblem {
    private final ProblemRepository problemRepository;
    //private final ServiceNotification serviceNotification;
    public ServiceProblem(ProblemRepository problemRepository            ) {

        this.problemRepository = problemRepository;    }

    ////////////////Resident//////////////////////
    public void reportProblem(Resident resident, String neighbourhood, String street,
                              TravauxType type, String coordinates, String description) {
        Problem newProblem = new Problem(street, neighbourhood, type, description);
        newProblem.setResident(resident);
        problemRepository.addProblem(newProblem);
    }

    public List <Problem> viewMyProblems(Resident resident) {
        return problemRepository.findByResident(resident.getId());
    }


    //////////////////Admin//////////////////////
    public List <Problem> viewAllProblems() {
        return problemRepository.getAllProblems();
    }

    public List<Problem> ViewproblemsNotAssigned() {
        return problemRepository.getAllProblems().stream()
                .filter(c -> c.getPrioriteType() == Priorite.NOT_ASSIGNED)// Ensure priorite is null
                .collect(Collectors.toList());
    }

    public Problem assignProblemPriority(int problemId, Priorite priorite) {
        try {
            Problem problem = problemRepository.findById(problemId).orElse(null);
    
            if (problem == null) {
                System.out.println("Problem not found with id: " + problemId);
                return null;
            }
    
            if (problem.getPrioriteType() != Priorite.NOT_ASSIGNED) {
                System.out.println("Problem already has a priority: " + problem.getPrioriteType());
                return problem;
            }
    
            problem.setPrioriteType(priorite);
            problemRepository.updateProblem(problem);
            return problem;
    
        } catch (Exception e) {
            System.out.println("Error assigning priority: " + e.getMessage());
            return null;
        }
    }
    
    }
