package ca.udem.maville.logic;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import ca.udem.maville.enums.StatutProjet;
import ca.udem.maville.model.Work;
import ca.udem.maville.repository.WorkRepository;

@Service
public class ServiceWork {
    private final WorkRepository workRepository;
    public ServiceWork(WorkRepository workRepository) {
        this.workRepository = workRepository;
    }
    /**
     * Allows a resident to view all works currently in progress.
     * @return A list of in-progress works.
     */
    public List<Work> viewInProgressWorks() {
        if (workRepository == null) {
            System.err.println("Work repository is not initialized. Cannot view in-progress works.");
            return Collections.emptyList();
        }
        return workRepository.findByStatus(StatutProjet.PERMIT_ISSUED);
    }
    public List<Work> viewFutureWorks() {
            LocalDate today = LocalDate.now();
            LocalDate threeMonthsLater = today.plusMonths(3); 
            List<Work> work = workRepository.findByDateBetween(today, threeMonthsLater);
            return work;
        }

    public List<Work> filterWorksByType(String type) {
        if (workRepository == null) {
            System.err.println("Work repository is not initialized. Cannot filter works by type.");
            return Collections.emptyList();
        }
        //String normalizedType = type.replace(" ", "_");
        return workRepository.findByCategory(type);
    }

    public List<Work> filterWorkByNeighbourdhood(String quartier) {
        return workRepository.findByNeighbourhood(quartier);
    }

    public List<Work> filterWorkByStreet(String quartier) {
        return workRepository.findByStreet(quartier);
    }




    


}
