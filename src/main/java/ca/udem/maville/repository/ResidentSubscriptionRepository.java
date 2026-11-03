package ca.udem.maville.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ca.udem.maville.model.AbonnementType;
import ca.udem.maville.model.ResidentSubscription;

@Repository
public interface ResidentSubscriptionRepository extends JpaRepository<ResidentSubscription, Long> {
    List<ResidentSubscription> findByResidentId(Long residentId);
    
    // Correct queries based on type+value
    List<ResidentSubscription> findByTypeAndSubscriptionValue(AbonnementType type, String subscriptionValue);
    
    // Optional: convenience methods
    default List<ResidentSubscription> findByNeighbourhood(String neighbourhood) {
        return findByTypeAndSubscriptionValue(AbonnementType.QUARTIER, neighbourhood);
    }
    
    default List<ResidentSubscription> findByStreet(String street) {
        return findByTypeAndSubscriptionValue(AbonnementType.RUE, street);
    }
}