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

    private String propertyType;
    private String contactNumber;
    private String notes;
    private String reviewerRemarks;
    private String documentPath;

    @Enumerated(EnumType.STRING)
    private Status status = Status.SUBMITTED;

    public enum Status {
        SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPropertyAddress() { return propertyAddress; }
    public void setPropertyAddress(String propertyAddress) { this.propertyAddress = propertyAddress; }

    public String getInspectorName() { return inspectorName; }
    public void setInspectorName(String inspectorName) { this.inspectorName = inspectorName; }

    public String getPropertyType() { return propertyType; }
    public void setPropertyType(String propertyType) { this.propertyType = propertyType; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getReviewerRemarks() { return reviewerRemarks; }
    public void setReviewerRemarks(String reviewerRemarks) { this.reviewerRemarks = reviewerRemarks; }

    public String getDocumentPath() { return documentPath; }
    public void setDocumentPath(String documentPath) { this.documentPath = documentPath; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}