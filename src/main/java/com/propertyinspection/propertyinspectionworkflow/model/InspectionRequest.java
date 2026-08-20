package com.propertyinspection.propertyinspectionworkflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
public class InspectionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Property address is required")
    private String propertyAddress;

    @NotBlank(message = "Inspector name is required")
    private String inspectorName;

    private String documentPath;

    @Enumerated(EnumType.STRING)
    private Status status = Status.SUBMITTED;

    public enum Status {
        SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPropertyAddress() { return propertyAddress; }
    public void setPropertyAddress(String propertyAddress) { this.propertyAddress = propertyAddress; }

    public String getInspectorName() { return inspectorName; }
    public void setInspectorName(String inspectorName) { this.inspectorName = inspectorName; }

    public String getDocumentPath() { return documentPath; }
    public void setDocumentPath(String documentPath) { this.documentPath = documentPath; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}