package ca.udem.maville.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.udem.maville.model.AbonnementType;
import ca.udem.maville.model.ProviderSubscription;


public interface ProviderSubscriptionRepository extends JpaRepository<ProviderSubscription, Long> {
    List<ProviderSubscription> findByProvider_CompanyNumber(String companyNumber);
    List<ProviderSubscription> findByTypeAndSubscriptionValue(AbonnementType type, String subscriptionValue);
    
    default List<ProviderSubscription> findByNeighbourhood(String neighbourhood) {
        return findByTypeAndSubscriptionValue(AbonnementType.QUARTIER, neighbourhood);
    }
    
    default List<ProviderSubscription> findByProblemType(String problemType) {
        return findByTypeAndSubscriptionValue(AbonnementType.TYPE_PROBLEME, problemType);
    }
}