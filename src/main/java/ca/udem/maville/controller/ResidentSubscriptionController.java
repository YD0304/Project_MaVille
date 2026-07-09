package ca.udem.maville.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.udem.maville.dto.SubscribeRequest;
import ca.udem.maville.model.Resident;
import ca.udem.maville.model.ResidentSubscription;
import ca.udem.maville.repository.ResidentRepository;
import ca.udem.maville.repository.ResidentSubscriptionRepository;
@RestController
@RequestMapping("/api/subscriptions/residents")
public class ResidentSubscriptionController {

    private final ResidentSubscriptionRepository subscriptionRepository;
    private final ResidentRepository residentRepository;

    public ResidentSubscriptionController(ResidentSubscriptionRepository subscriptionRepository,
                                          ResidentRepository residentRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.residentRepository = residentRepository;
    }

    // Create a new subscription
    @PostMapping
    public ResponseEntity<ResidentSubscription> subscribe(@RequestBody SubscribeRequest request) {
        Resident resident = residentRepository.findById(request.getResidentId())
                .orElseThrow(() -> new IllegalArgumentException("Resident not found"));
        ResidentSubscription sub = new ResidentSubscription(resident, request.getType(), request.getValue(), true);
        return ResponseEntity.ok(subscriptionRepository.save(sub));
    }

    // Get all subscriptions for a resident
    @GetMapping
    public ResponseEntity<List<ResidentSubscription>> getSubscriptions(@RequestParam Long residentId) {
        List<ResidentSubscription> subs = subscriptionRepository.findByResidentId(residentId);
        return ResponseEntity.ok(subs);
    }

    // Soft delete (deactivate) a subscription
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> unsubscribe(@PathVariable Long id) {
        ResidentSubscription sub = subscriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));
        sub.setActive(false);
        subscriptionRepository.save(sub);
        return ResponseEntity.ok().build();
    }

    // Reactivate a subscription
    @PutMapping("/{id}/reactivate")
    public ResponseEntity<ResidentSubscription> reactivate(@PathVariable Long id) {
        ResidentSubscription sub = subscriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));
        sub.setActive(true);
        return ResponseEntity.ok(subscriptionRepository.save(sub));
    }
}