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
import ca.udem.maville.repository.CandidatureProjetRepository;
import ca.udem.maville.repository.PrestataireDAO;
import ca.udem.maville.repository.ProblemRepository;
import ca.udem.maville.repository.WorkRepository;

@Service
public class ServiceProject { 
    private final ProblemRepository problems;
    private final CandidatureProjetRepository candidatureDAO;
    private final PrestataireDAO prestataireDAO;

    public ServiceProject(ProblemRepository problems,
            CandidatureProjetRepository candidatureDAO,
            PrestataireDAO prestataireDAO) {
        this.problems = problems;
        this.candidatureDAO = candidatureDAO;
        this.prestataireDAO = prestataireDAO;
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

    public boolean updateProjectStatus(int projetId, Prestataire serviceProvider, StatutProjet newStatus) {
        List<work> candidatures = viewMyProposals(serviceProvider);
        Optional<Candidature> existing = candidatures.stream()
                .filter(c -> c.getId() == projetId)
                .findFirst();
        if (existing.isPresent()) {
            Candidature candidature = existing.get();
            candidature.setStatus(newStatus);
            candidatureDAO.save(candidature);
            return true;
        }
        return false;
    }

    public boolean updateProjectDescription(int projetId, Prestataire serviceProvider, String newDescription) {
        List<Candidature> candidatures = viewMyProposals(serviceProvider);
        Optional<Candidature> existing = candidatures.stream()
                .filter(c -> c.getId() == projetId)
                .findFirst();
        if (existing.isPresent()) {
            Candidature candidature = existing.get();
            candidature.setDescription(newDescription);
            candidatureDAO.save(candidature);
            return true;
        }
        return false;

    }

    public boolean updateProjectEndDate(int projetId, Prestataire serviceProvider, String newDate) {
    if (newDate == null || newDate.isEmpty()) {
        return false;
    }
    // Valider le format de date
    try {
        LocalDate endDate = LocalDate.parse(newDate);
        
        List<Candidature> candidatures = viewMyProposals(serviceProvider);
        Optional<Candidature> existing = candidatures.stream()
            .filter(c -> c.getId() == projetId)
            .findFirst();
            
        if (existing.isPresent()) {
            Candidature candidature = existing.get();
            
            // Vérifier que la date de fin est après la date de début
            LocalDate startDate = LocalDate.parse(candidature.getStartDate());
            if (endDate.isBefore(startDate)) {
                return false;
            }
            
            candidature.setEndDate(newDate);
            candidatureDAO.save(candidature);
            
            // Notifier les résidents abonnés
            // serviceNotification.notifierMiseAJourProjet(candidature);
            
            return true;
        }
    } catch (DateTimeParseException e) {
        return false;
    }
    return false;
}

    ///////////STPM/////////////////////////
    public List<Candidature> proposalsSubmited() {
        List<Candidature> projects = candidatureDAO.getAllCandidatures().stream()
                .filter(p -> p.getStatus().equals(StatutProjet.PROPOSAL_SUBMITED))
                .collect(Collectors.toList());
        return projects != null ? projects : Collections.emptyList();
    }

    public Work proposalEvaluation(int projectId, boolean accepter) {
        Candidature projet = candidatureDAO.findById(projectId);
        if (projet == null) {
            return ;
        }
        
        if (projet.getStatus() != StatutProjet.PROPOSAL_SUBMITED) {
            return false;
        }
        
        projet.setStatus(StatutProjet.PROPOSAL_ACCEPTED);
        candidatureDAO.save(projet);
        WorkRepository.getInstance().addWork(Work.fromProject(projet));
        return;
    }
}