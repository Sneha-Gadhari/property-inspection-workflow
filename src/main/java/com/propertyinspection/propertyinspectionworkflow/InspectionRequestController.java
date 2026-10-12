package com.propertyinspection.propertyinspectionworkflow;

import com.propertyinspection.propertyinspectionworkflow.model.InspectionRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

    @GetMapping("/statistics")
    public Map<String, Object> getStatistics() {
        List<InspectionRequest> all = repository.findAll();
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalRequests", all.size());

        Map<String, Long> statusCounts = new LinkedHashMap<>();
        for (InspectionRequest.Status s : InspectionRequest.Status.values()) {
            statusCounts.put(s.name(), all.stream().filter(r -> r.getStatus() == s).count());
        }
        stats.put("countsByStatus", statusCounts);

        Map<String, Long> typeCounts = all.stream()
                .collect(Collectors.groupingBy(
                        r -> (r.getPropertyType() == null || r.getPropertyType().isBlank()) ? "Unspecified" : r.getPropertyType(),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));
        stats.put("countsByPropertyType", typeCounts);

        Map<String, Long> urgencyCounts = all.stream()
                .collect(Collectors.groupingBy(
                        r -> (r.getUrgency() == null || r.getUrgency().isBlank()) ? "MEDIUM" : r.getUrgency(),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));
        stats.put("countsByUrgency", urgencyCounts);

        long approved = statusCounts.getOrDefault("APPROVED", 0L);
        long rejected = statusCounts.getOrDefault("REJECTED", 0L);
        long resolved = approved + rejected;
        double approvalRate = resolved > 0 ? (approved * 100.0) / resolved : (all.isEmpty() ? 0.0 : (approved * 100.0) / all.size());
        stats.put("approvalRate", Math.round(approvalRate * 10.0) / 10.0);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, Long> overTime = all.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCreatedAt() != null ? r.getCreatedAt().format(fmt) : LocalDate.now().format(fmt),
                        TreeMap::new,
                        Collectors.counting()
                ));
        List<Map<String, Object>> timeline = overTime.entrySet().stream()
                .map(e -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("date", e.getKey());
                    m.put("count", e.getValue());
                    return m;
                })
                .toList();
        stats.put("requestsOverTime", timeline);

        return stats;
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        return getStatistics();
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

    @PutMapping("/{id}/start-review")
    public InspectionRequest startReview(@PathVariable Long id) {
        InspectionRequest r = repository.findById(id).orElseThrow();
        r.setStatus(InspectionRequest.Status.UNDER_REVIEW);
        return repository.save(r);
    }

    @PutMapping("/{id}/approve")
    public InspectionRequest approve(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        InspectionRequest r = repository.findById(id).orElseThrow();
        r.setStatus(InspectionRequest.Status.APPROVED);
        if (body != null && body.get("remarks") != null) {
            r.setReviewerRemarks(body.get("remarks"));
        }
        return repository.save(r);
    }

    @PutMapping("/{id}/reject")
    public InspectionRequest reject(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        InspectionRequest r = repository.findById(id).orElseThrow();
        r.setStatus(InspectionRequest.Status.REJECTED);
        if (body != null && body.get("remarks") != null) {
            r.setReviewerRemarks(body.get("remarks"));
        }
        return repository.save(r);
    }
}