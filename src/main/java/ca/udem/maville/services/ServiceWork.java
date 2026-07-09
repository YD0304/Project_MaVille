// package ca.udem.maville.services;

// import java.time.LocalDate;
// import java.util.Collections;
// import java.util.List;

// import org.springframework.stereotype.Service;

// import ca.udem.maville.model.StatutProjet;
// import ca.udem.maville.model.Work;
// import ca.udem.maville.repository.WorkRepository;

// // ca.udem.maville.services.ServiceWork
// @Service
// public class ServiceWork {
//     private final WorkRepository workRepository;

//     public ServiceWork(WorkRepository workRepository) {
//         this.workRepository = workRepository;
//     }

//     /**
//      * Retourne les travaux dont le statut est "PROJECT_ONGOING" (en cours)
//      */
//     public List<Work> viewInProgressWorks() {
//         if (workRepository == null) {
//             System.err.println("Work repository is not initialized.");
//             return Collections.emptyList();
//         }
//         return workRepository.findByStatus(StatutProjet.PROJECT_ONGOING);  // corrigé
//     }

//     /**
//      * Retourne les travaux dont la date de début prévue est entre aujourd'hui et dans 3 mois.
//      */
//     public List<Work> viewFutureWorks() {
//         LocalDate today = LocalDate.now();
//         LocalDate threeMonthsLater = today.plusMonths(3);
//         return workRepository.findByStartDateBetween(today, threeMonthsLater);
//     }

//     public List<Work> filterWorksByType(String type) {
//         if (workRepository == null) {
//             System.err.println("Work repository is not initialized.");
//             return Collections.emptyList();
//         }
//         return workRepository.findByCategory(type);
//     }

//     public List<Work> filterWorkByNeighbourdhood(String neighbourhood) {
//         return workRepository.findByNeighbourhood(neighbourhood);
//     }

//     public List<Work> filterWorkByStreet(String street) {
//         return workRepository.findByStreet(street);
//     }

//     public List<Work> getServiceProviderWorks(String serviceProviderName) {
//         // On suppose que le nom du prestataire correspond à provider.companyName
//         return workRepository.findByProviderCompanyName(serviceProviderName);
//     }
// }