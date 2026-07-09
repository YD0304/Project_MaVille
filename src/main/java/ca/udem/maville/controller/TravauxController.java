package ca.udem.maville.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.udem.maville.api.TravauxAPIRepository;
import ca.udem.maville.model.MontrealAPIWork;

@RestController
@RequestMapping("/api/travaux")
public class TravauxController {

    private final TravauxAPIRepository travauxRepository;

    public TravauxController(TravauxAPIRepository travauxRepository) {
        this.travauxRepository = travauxRepository;
    }

    @GetMapping
    public ResponseEntity<List<MontrealAPIWork>> getAllTravaux() {
        return ResponseEntity.ok(travauxRepository.getAllAPIWorks());
    }

    @GetMapping("/filter")
    public ResponseEntity<List<MontrealAPIWork>> filterTravaux(
            @RequestParam String filterKey,
            @RequestParam String filterValue) {
        List<MontrealAPIWork> result = travauxRepository.filterTravauxExterneList(filterKey, filterValue);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllTravaux() {
        travauxRepository.supprimerTousLesTravauxExternes();
        return ResponseEntity.noContent().build();
    }
}
