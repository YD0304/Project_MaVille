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
import ca.udem.maville.model.Provider;
import ca.udem.maville.model.ProviderSubscription;
import ca.udem.maville.repository.ProviderRepository;
import ca.udem.maville.repository.ProviderSubscriptionRepository;

@RestController
@RequestMapping("/api/subscriptions/providers")
public class ProviderSubscriptionController {

    private final ProviderSubscriptionRepository subscriptionRepository;
    private final ProviderRepository providerRepository;

    public ProviderSubscriptionController(ProviderSubscriptionRepository subscriptionRepository,
                                          ProviderRepository providerRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.providerRepository = providerRepository;
    }

    @PostMapping
    public ResponseEntity<ProviderSubscription> subscribe(@RequestBody SubscribeRequest request) {
        Provider provider = providerRepository.findById(request.getProviderCompanyNumber())
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));
        ProviderSubscription sub = new ProviderSubscription(provider, request.getType(), request.getValue(), true);
        return ResponseEntity.ok(subscriptionRepository.save(sub));
    }

    @GetMapping
    public ResponseEntity<List<ProviderSubscription>> getSubscriptions(@RequestParam String companyNumber) {
        List<ProviderSubscription> subs = subscriptionRepository.findByProvider_CompanyNumber(companyNumber);
        return ResponseEntity.ok(subs);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> unsubscribe(@PathVariable Long id) {
        ProviderSubscription sub = subscriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));
        sub.setActive(false);
        subscriptionRepository.save(sub);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/reactivate")
    public ResponseEntity<ProviderSubscription> reactivate(@PathVariable Long id) {
        ProviderSubscription sub = subscriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));
        sub.setActive(true);
        return ResponseEntity.ok(subscriptionRepository.save(sub));
    }
}