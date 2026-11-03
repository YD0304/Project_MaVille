package ca.udem.maville.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.udem.maville.model.Notification;
import ca.udem.maville.model.Problem;
import ca.udem.maville.model.Project;
import ca.udem.maville.model.ProjectEvent;
import ca.udem.maville.model.ProjectStatus;
import ca.udem.maville.model.ProviderSubscription;
import ca.udem.maville.model.ResidentSubscription;
import ca.udem.maville.repository.NotificationRepository;
import ca.udem.maville.repository.ProviderSubscriptionRepository;
import ca.udem.maville.repository.ResidentSubscriptionRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ResidentSubscriptionRepository residentSubRepo;
    private final ProviderSubscriptionRepository providerSubRepo;
    private final WebSocketNotificationService wsNotificationService;

    public NotificationService(NotificationRepository notificationRepository,
                               ResidentSubscriptionRepository residentSubRepo,
                               ProviderSubscriptionRepository providerSubRepo,
                               WebSocketNotificationService wsNotificationService) {
        this.notificationRepository = notificationRepository;
        this.residentSubRepo = residentSubRepo;
        this.providerSubRepo = providerSubRepo;
        this.wsNotificationService = wsNotificationService;
    }

    @Transactional
    public void notifyResidentsAboutProject(Project project, ProjectEvent event) {
        String message = buildProjectMessage(project, event);
        String neighbourhood = project.getProblem().getNeighbourhood();
        String street = project.getProblem().getStreet();

        List<ResidentSubscription> combined = new ArrayList<>(residentSubRepo.findByNeighbourhood(neighbourhood));
        combined.addAll(residentSubRepo.findByStreet(street));

        Set<Long> seen = new HashSet<>();
        for (ResidentSubscription sub : combined) {
            if (seen.add(sub.getResident().getId())) {
                createAndSend(sub.getResident().getEmail(), "RESIDENT", message, project.getId());
            }
        }
    }

    @Transactional
    public void notifyProvidersAboutNewProblem(Problem problem) {
        String message = "New problem reported: " + problem.getType().name() + " in " + problem.getNeighbourhood();

        List<ProviderSubscription> combined = new ArrayList<>(providerSubRepo.findByNeighbourhood(problem.getNeighbourhood()));
        combined.addAll(providerSubRepo.findByProblemType(problem.getType().name()));

        Set<String> seen = new HashSet<>();
        for (ProviderSubscription sub : combined) {
            if (seen.add(sub.getProvider().getCompanyNumber())) {
                createAndSend(sub.getProvider().getCompanyNumber(), "PROVIDER", message, problem.getId());
            }
        }
    }

    // STPM has a single generic account
    @Transactional
    public void notifySTPM(String message, Long relatedId) {
        createAndSend("STPM_AGENT", "STPM", message, relatedId);
    }

    @Transactional
    public void notifyProviderAboutDecision(Project project) {
        boolean rejected = project.getStatus() == ProjectStatus.PROPOSAL_REFUSED;
        String message = "Your proposal for project '" + project.getTitle() + "' has been "
                + (rejected ? "REJECTED" : "ACCEPTED");
        if (rejected && project.getRejectionReason() != null && !project.getRejectionReason().isBlank()) {
            message += ". Reason: " + project.getRejectionReason();
        }
        createAndSend(project.getProvider().getCompanyNumber(), "PROVIDER", message, project.getId());
    }

    public List<Notification> getNotifications(String userId, String userType) {
        return notificationRepository.findByUserIdAndUserTypeOrderByCreatedAtDesc(userId, userType);
    }

    public List<Notification> getUnreadNotifications(String userId, String userType) {
        return notificationRepository.findByUserIdAndUserTypeAndReadFalse(userId, userType);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> n.setRead(true));
    }

    private void createAndSend(String userId, String userType, String message, Long relatedId) {
        Notification notif = new Notification();
        notif.setUserId(userId);
        notif.setUserType(userType);
        notif.setMessage(message);
        notif.setRelatedEntityId(relatedId);
        notif.setRead(false);
        notif.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notif);
        wsNotificationService.pushNotification(notif);
    }

    private String buildProjectMessage(Project project, ProjectEvent event) {
        switch (event) {
            case CREATED:
                return "New project created: " + project.getTitle() + " starting " + project.getProposedStartDate();
            case STATUS_CHANGED:
                return "Project '" + project.getTitle() + "' status changed to " + project.getStatus();
            case DATE_UPDATED:
                return "Project '" + project.getTitle() + "' end date updated to " + project.getProposedEndDate();
            default:
                return "Project '" + project.getTitle() + "' has been updated.";
        }
    }
}
