package ca.udem.maville.logic;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ca.udem.maville.enums.Priorite;
import ca.udem.maville.enums.StatutProjet;
import ca.udem.maville.model.Candidature;
import ca.udem.maville.model.Prestataire; // Ensure this is the correct package for the Problem class
import ca.udem.maville.model.Problem; // Ensure this is the correct package for the Candidature class
import ca.udem.maville.model.Work;
import ca.udem.maville.repository.CandidatureProjetRepository;
import ca.udem.maville.repository.ProblemRepository;
import ca.udem.maville.repository.WorkRepository;

@Service
public class ServiceProject { 
    private final ProblemRepository problems;
    private final CandidatureProjetRepository candidatureDAO;
    private final WorkRepository workRepository;

    public ServiceProject(ProblemRepository problems,
            CandidatureProjetRepository candidatureDAO,
            WorkRepository workRepository) {
        this.problems = problems;
        this.candidatureDAO = candidatureDAO;
        this.workRepository = workRepository;
    }

   public Candidature submitProposalForProblem(int problemId, String title, String description,
                                        String startDate, String endDate, double cost, Prestataire prestataire) {
    // 1. Vérifier que le problème existe et a une priorité
    Problem problem = problems.findById(problemId).orElse(null);
    if (problem == null || problem.getPrioriteType() == Priorite.NOT_ASSIGNED) {
        return null;
    }
    
    Candidature newCandidature = new Candidature(
        0,
        title,
        problem,
        description,
        cost,
        startDate,
        endDate,
        StatutProjet.PROPOSAL_SUBMITED,
        prestataire
    );

    // 3. Sauvegarder la candidature
    candidatureDAO.save(newCandidature);
    
    // 4. Notifier le STPM
    // serviceNotification.notifierNouvelleCandidature(newCandidature);
    
    return newCandidature;
}

    public List<Candidature> viewMyProposals(Prestataire serviceProvider) {
        return candidatureDAO.findByServiceProviderId(serviceProvider);
    }


    public Work updateWorkStatus(int projetId, Prestataire serviceProvider, StatutProjet newStatus) {
        Optional<Work> optional = workRepository.findById(projetId);
        if (optional.isPresent()) {
            Work work = optional.get();
            if (!"LOCAL_PROJECT".equals(work.getSource())) {
                return null;
            }
            if (!work.getServiceProvider().equals(serviceProvider.getNomEntreprise())) {
                return null; 
            }
            work.setStatus(newStatus);
            workRepository.update(work);
            return work;
        }
        return null;
    }

    

    public Work updateWorkDescription(int projetId, Prestataire serviceProvider, String newDescription) {
        Optional<Work> optional = workRepository.findById(projetId);
        if (optional.isPresent()) {
            Work work = optional.get();
            if (!"LOCAL_PROJECT".equals(work.getSource())) {
                return null; // cannot update API work
            }

            if (!work.getServiceProvider().equals(serviceProvider.getNomEntreprise())) {
                return null; // only the prestataire owner can modify
            }
            work.setDescription(newDescription);
            workRepository.update(work);
            return work;
        }
        return null;
    }
    
    public Work updateWorktEndDate(int projetId, Prestataire serviceProvider, String newDate) {
    // Valider le format de date
    try {
        LocalDate endDate = LocalDate.parse(newDate);
        
        Optional<Work> optional = workRepository.findById(projetId);
        if (optional.isPresent()) {
            Work work = optional.get();
            if (!"LOCAL_PROJECT".equals(work.getSource())) {
                return null; // cannot update API work
            }

            if (!work.getServiceProvider().equals(serviceProvider.getNomEntreprise())) {
                return null; // only the prestataire owner can modify
            }
            LocalDate startDate = work.getStartDate();
            if (endDate.isBefore(startDate)) {
                return null; // end date must be after start date
            }
            work.setEndDate(endDate);
            workRepository.update(work);
            return work;
        }
    } catch (DateTimeParseException e) {
    }
    return null;
}

    ///////////STPM/////////////////////////
    public List<Candidature> proposalsSubmited() {
        List<Candidature> projects = candidatureDAO.getAllCandidatures().stream()
                .filter(p -> p.getStatus().equals(StatutProjet.PROPOSAL_SUBMITED))
                .collect(Collectors.toList());
        return projects != null ? projects : Collections.emptyList();
    }

    public Work proposalEvaluation(int projectId, StatutProjet status) {
        Candidature project = candidatureDAO.findById(projectId);
        if (project == null) {
            return null;
            }
        
        if (project.getStatus() != StatutProjet.PROPOSAL_SUBMITED) {
            return null; // Only proposals in SUBMITED status can be evaluated
            }
        project.setStatus(status);
        Work.fromProject(project);
        workRepository.updateAndGetAllWork(); // Ajouter le projet aux travaux
        candidatureDAO.deleteById(projectId);
        return Work.fromProject(project);
    }
    
    public List<Candidature> viewAllProposals() {
        List<Candidature> projects = candidatureDAO.getAllCandidatures();
        return projects != null ? projects : Collections.emptyList();
    }}