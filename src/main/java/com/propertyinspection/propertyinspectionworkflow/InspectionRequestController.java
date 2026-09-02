package com.propertyinspection.propertyinspectionworkflow;

import com.propertyinspection.propertyinspectionworkflow.model.InspectionRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inspections")
public class InspectionRequestController {

    private final InspectionRequestRepository repository;

    public InspectionRequestController(InspectionRequestRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public InspectionRequest submit(@Valid @RequestBody InspectionRequest request) {
        return repository.save(request);
    }

    @GetMapping
    public List<InspectionRequest> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public InspectionRequest getOne(@PathVariable Long id) {
        return repository.findById(id).orElseThrow();
    }

    @GetMapping("/status/{status}")
    public List<InspectionRequest> getByStatus(@PathVariable InspectionRequest.Status status) {
        return repository.findAll().stream()
                .filter(r -> r.getStatus() == status)
                .toList();
    }
    @PutMapping("/{id}/approve")
    public InspectionRequest approve(@PathVariable Long id) {
        InspectionRequest r = repository.findById(id).orElseThrow();
        r.setStatus(InspectionRequest.Status.APPROVED);
        return repository.save(r);
    }

    @PutMapping("/{id}/reject")
    public InspectionRequest reject(@PathVariable Long id) {
        InspectionRequest r = repository.findById(id).orElseThrow();
        r.setStatus(InspectionRequest.Status.REJECTED);
        return repository.save(r);
    }

    @PutMapping("/{id}/start-review")
    public InspectionRequest startReview(@PathVariable Long id) {
        InspectionRequest r = repository.findById(id).orElseThrow();
        r.setStatus(InspectionRequest.Status.UNDER_REVIEW);
        return repository.save(r);
    }
}