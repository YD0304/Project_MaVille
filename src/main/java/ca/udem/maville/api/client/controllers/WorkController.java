package ca.udem.maville.api.client.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.udem.maville.logic.ServiceWork;
import ca.udem.maville.model.Work;

@RestController
@RequestMapping("/api/works")
public class WorkController {
    private final ServiceWork serviceWork;

    public WorkController(ServiceWork serviceWork) {
        this.serviceWork = serviceWork;
    }

    @GetMapping("/in_progress_works")
    public ResponseEntity<List<Work>> getInProgressWorks() {
        return ResponseEntity.ok(serviceWork.viewInProgressWorks());
    }

    @GetMapping("/upcoming_works")
    public ResponseEntity<List<Work>> getUpcomingWorks() {
        // Fixed: calling the correct method
        return ResponseEntity.ok(serviceWork.viewFutureWorks());
    }

    @GetMapping("/filter_by_type")  
    public ResponseEntity<List<Work>> filterWorksByType(@RequestParam String type) {
        return ResponseEntity.ok(serviceWork.filterWorksByType(type));
    }

    @GetMapping("/filter_by_neighbourhood")  
    public ResponseEntity<List<Work>> filterWorkByNeighbourdhood(@RequestParam String neighbourhood) {
        return ResponseEntity.ok(serviceWork.filterWorkByNeighbourdhood(neighbourhood));
    }

    @GetMapping("/filter_by_street")
    public ResponseEntity<List<Work>> filterWorkByStreet(@RequestParam String street) {
        return ResponseEntity.ok(serviceWork.filterWorkByStreet(street));
    }
}
