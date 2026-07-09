package ca.udem.maville.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.udem.maville.model.Notification;
import ca.udem.maville.model.Problem;
import ca.udem.maville.model.Project;
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

    // ---- For Residents: when a project is created or updated ----
    @Transactional
    public void notifyResidentsAboutProject(Project project, String eventType) {
        // eventType: "CREATED", "STATUS_CHANGED", "DATE_UPDATED"
        String message = buildProjectMessage(project, eventType);
        // Find residents subscribed to the project's neighbourhood or street
        String neighbourhood = project.getProblem().getNeighbourhood(); // adjust based on your Problem entity
        String street = project.getProblem().getStreet();

        List<ResidentSubscription> matchingSubs = residentSubRepo.findByNeighbourhood(neighbourhood);
        matchingSubs.addAll(residentSubRepo.findByStreet(street));
        // avoid duplicates
        List<ResidentSubscription> distinct = matchingSubs.stream()
                .distinct()
                .collect(Collectors.toList());

        for (ResidentSubscription sub : distinct) {
            Notification notif = new Notification();
            notif.setUserId(sub.getResident().getEmail());
            notif.setUserType("RESIDENT");
            notif.setMessage(message);
            notif.setRelatedEntityId(project.getId());
            notif.setRead(false);
            notif.setCreatedAt(LocalDateTime.now());
            notificationRepository.save(notif);
            wsNotificationService.pushNotification(notif);
        }
    }

    // ---- For Providers: when a new problem is reported (fiche problème) ----
    @Transactional
    public void notifyProvidersAboutNewProblem(Problem problem) {
        String message = "New problem reported: " + problem.getType().name() + " in " + problem.getNeighbourhood();
        // Find providers subscribed to this neighbourhood or problem type
        List<ProviderSubscription> matching = providerSubRepo.findByNeighbourhood(problem.getNeighbourhood());
        matching.addAll(providerSubRepo.findByProblemType(problem.getType().name()));

        for (ProviderSubscription sub : matching) {
            Notification notif = new Notification();
            notif.setUserId(sub.getProvider().getCompanyNumber());
            notif.setUserType("PROVIDER");
            notif.setMessage(message);
            notif.setRelatedEntityId(problem.getId());
            notif.setRead(false);
            notif.setCreatedAt(LocalDateTime.now());
            notificationRepository.save(notif);
            wsNotificationService.pushNotification(notif);
        }
    }

    // ---- For STPM: when a new problem is reported or new proposal submitted ----
    @Transactional
    public void notifySTPM(String message, Long relatedId) {
        // STPM has a single generic account (e.g., userId = "STPM_AGENT")
        Notification notif = new Notification();
        notif.setUserId("STPM_AGENT");
        notif.setUserType("STPM");
        notif.setMessage(message);
        notif.setRelatedEntityId(relatedId);
        notif.setRead(false);
        notif.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notif);
        wsNotificationService.pushNotification(notif);
    }

    // ---- For Provider: when decision on proposal is made ----
    @Transactional
    public void notifyProviderAboutDecision(Project project) {
        boolean rejected = project.getStatus() == ProjectStatus.PROPOSAL_REFUSED;
        String message = "Your proposal for project '" + project.getTitle() + "' has been " +
                         (rejected ? "REJECTED" : "ACCEPTED");
        if (rejected && project.getRejectionReason() != null && !project.getRejectionReason().isBlank()) {
            message += ". Reason: " + project.getRejectionReason();
        }
        Notification notif = new Notification();
        notif.setUserId(project.getProvider().getCompanyNumber());
        notif.setUserType("PROVIDER");
        notif.setMessage(message);
        notif.setRelatedEntityId(project.getId());
        notif.setRead(false);
        notif.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notif);
        wsNotificationService.pushNotification(notif);
    }

    // Helper to build readable project message
    private String buildProjectMessage(Project project, String eventType) {
        switch (eventType) {
            case "CREATED":
                return "New project created: " + project.getTitle() + " starting " + project.getProposedStartDate();
            case "STATUS_CHANGED":
                return "Project '" + project.getTitle() + "' status changed to " + project.getStatus();
            case "DATE_UPDATED":
                return "Project '" + project.getTitle() + "' end date updated to " + project.getProposedEndDate();
            default:
                return "Project '" + project.getTitle() + "' has been updated.";
        }
    }

    // ---- Retrieve notifications for a user ----
    public List<Notification> getNotifications(String userId, String userType) {
        return notificationRepository.findByUserIdAndUserTypeOrderByCreatedAtDesc(userId, userType);
    }

    public List<Notification> getUnreadNotifications(String userId, String userType) {
        return notificationRepository.findByUserIdAndUserTypeAndReadFalse(userId, userType);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }
}