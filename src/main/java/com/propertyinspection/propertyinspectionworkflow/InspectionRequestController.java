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
        appendAudit(r, "Review Started", "Reviewer", "Desk review initiated");
        return repository.save(r);
    }

    @PutMapping("/{id}/assign-inspector")
    public InspectionRequest assignInspector(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        InspectionRequest r = repository.findById(id).orElseThrow();
        if (body.get("inspectorName") != null) {
            r.setInspectorName(body.get("inspectorName").toString());
        }
        if (body.get("scheduledDate") != null) {
            r.setScheduledDate(body.get("scheduledDate").toString());
        }
        if (body.get("feeAmount") != null) {
            try {
                r.setFeeAmount(Double.parseDouble(body.get("feeAmount").toString()));
            } catch (Exception ignored) {}
        }
        if (body.get("remarks") != null && !body.get("remarks").toString().isBlank()) {
            r.setReviewerRemarks(body.get("remarks").toString());
        }
        r.setStatus(InspectionRequest.Status.UNDER_REVIEW);
        String reviewerName = body.getOrDefault("reviewer", "Reviewer").toString();
        appendAudit(r, "Inspector Assigned", reviewerName, "Assigned to " + r.getInspectorName() + " for " + (r.getScheduledDate() != null ? r.getScheduledDate() : "TBD"));
        return repository.save(r);
    }

    @PutMapping("/{id}/conduct-inspection")
    public InspectionRequest conductInspection(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        InspectionRequest r = repository.findById(id).orElseThrow();
        if (body.get("checklistSummary") != null) r.setChecklistSummary(body.get("checklistSummary").toString());
        if (body.get("checklistJson") != null) r.setChecklistJson(body.get("checklistJson").toString());
        if (body.get("defectListJson") != null) r.setDefectListJson(body.get("defectListJson").toString());
        if (body.get("overallCondition") != null) r.setOverallCondition(body.get("overallCondition").toString());
        if (body.get("inspectorSignature") != null) r.setInspectorSignature(body.get("inspectorSignature").toString());
        if (body.get("inspectorLicense") != null) r.setInspectorLicense(body.get("inspectorLicense").toString());
        if (body.get("notes") != null) r.setNotes(body.get("notes").toString());

        if (body.get("safetyHazardsCount") != null) {
            try { r.setSafetyHazardsCount(Integer.parseInt(body.get("safetyHazardsCount").toString())); } catch (Exception ignored) {}
        }
        if (body.get("majorDefectsCount") != null) {
            try { r.setMajorDefectsCount(Integer.parseInt(body.get("majorDefectsCount").toString())); } catch (Exception ignored) {}
        }
        if (body.get("minorDefectsCount") != null) {
            try { r.setMinorDefectsCount(Integer.parseInt(body.get("minorDefectsCount").toString())); } catch (Exception ignored) {}
        }
        appendAudit(r, "Field Audit Submitted", r.getInspectorName(), "On-site audit completed. Rating: " + r.getOverallCondition());
        return repository.save(r);
    }

    @PutMapping("/{id}/sign-agreement")
    public InspectionRequest signAgreement(@PathVariable Long id, @RequestBody Map<String, String> body) {
        InspectionRequest r = repository.findById(id).orElseThrow();
        r.setAgreementSigned(true);
        if (body != null && body.get("clientSignature") != null) {
            r.setClientSignature(body.get("clientSignature"));
        }
        appendAudit(r, "Agreement Signed", r.getOwnerName() != null ? r.getOwnerName() : "Client", "Signed pre-inspection agreement and disclaimer terms");
        return repository.save(r);
    }

    @PutMapping("/{id}/process-payment")
    public InspectionRequest processPayment(@PathVariable Long id, @RequestBody Map<String, String> body) {
        InspectionRequest r = repository.findById(id).orElseThrow();
        String status = (body != null && body.get("paymentStatus") != null) ? body.get("paymentStatus") : "PAID";
        r.setPaymentStatus(status);
        appendAudit(r, "Payment Updated", "Billing", "Inspection fee status: " + status);
        return repository.save(r);
    }

    @PutMapping("/{id}/submit-feedback")
    public InspectionRequest submitFeedback(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        InspectionRequest r = repository.findById(id).orElseThrow();
        if (body.get("clientRating") != null) {
            try { r.setClientRating(Integer.parseInt(body.get("clientRating").toString())); } catch (Exception ignored) {}
        }
        if (body.get("clientFeedback") != null) {
            r.setClientFeedback(body.get("clientFeedback").toString());
        }
        appendAudit(r, "Feedback Submitted", r.getOwnerName() != null ? r.getOwnerName() : "Client", "Rating: " + r.getClientRating() + " stars");
        return repository.save(r);
    }

    @PutMapping("/{id}/approve")
    public InspectionRequest approve(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        InspectionRequest r = repository.findById(id).orElseThrow();
        r.setStatus(InspectionRequest.Status.APPROVED);
        if (body != null && body.get("remarks") != null) {
            r.setReviewerRemarks(body.get("remarks"));
        }
        appendAudit(r, "Inspection Approved", "Reviewer", "Official compliance certification granted. Remarks: " + (r.getReviewerRemarks() != null ? r.getReviewerRemarks() : "None"));
        return repository.save(r);
    }

    @PutMapping("/{id}/reject")
    public InspectionRequest reject(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        InspectionRequest r = repository.findById(id).orElseThrow();
        r.setStatus(InspectionRequest.Status.REJECTED);
        if (body != null && body.get("remarks") != null) {
            r.setReviewerRemarks(body.get("remarks"));
        }
        appendAudit(r, "Inspection Rejected", "Reviewer", "Deficiency notice issued. Remarks: " + (r.getReviewerRemarks() != null ? r.getReviewerRemarks() : "None"));
        return repository.save(r);
    }

    private void appendAudit(InspectionRequest r, String action, String by, String detail) {
        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String safeAction = action != null ? action.replace("\"", "'") : "Update";
        String safeBy = by != null ? by.replace("\"", "'") : "User";
        String safeDetail = detail != null ? detail.replace("\"", "'") : "";
        String newEntry = "{\"time\":\"" + timestamp + "\",\"action\":\"" + safeAction + "\",\"by\":\"" + safeBy + "\",\"detail\":\"" + safeDetail + "\"}";
        String current = r.getAuditTrailJson();
        if (current == null || current.isBlank() || !current.trim().startsWith("[")) {
            r.setAuditTrailJson("[" + newEntry + "]");
        } else {
            String trimmed = current.trim();
            if (trimmed.endsWith("]")) {
                r.setAuditTrailJson(trimmed.substring(0, trimmed.length() - 1) + "," + newEntry + "]");
            } else {
                r.setAuditTrailJson("[" + newEntry + "]");
            }
        }
    }
}