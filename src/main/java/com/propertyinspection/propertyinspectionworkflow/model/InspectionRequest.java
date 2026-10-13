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

    private String ownerName;
    private String preferredDate;
    private String urgency = "MEDIUM";
    private Integer numRooms;
    private Integer numFloors;
    private Double areaSqFt;
    private String checklistSummary;
    private java.time.LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = java.time.LocalDateTime.now();
        }
        if (this.urgency == null || this.urgency.isBlank()) {
            this.urgency = "MEDIUM";
        }
    }

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

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getPreferredDate() { return preferredDate; }
    public void setPreferredDate(String preferredDate) { this.preferredDate = preferredDate; }

    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }

    public Integer getNumRooms() { return numRooms; }
    public void setNumRooms(Integer numRooms) { this.numRooms = numRooms; }

    public Integer getNumFloors() { return numFloors; }
    public void setNumFloors(Integer numFloors) { this.numFloors = numFloors; }

    public Double getAreaSqFt() { return areaSqFt; }
    public void setAreaSqFt(Double areaSqFt) { this.areaSqFt = areaSqFt; }

    public String getChecklistSummary() { return checklistSummary; }
    public void setChecklistSummary(String checklistSummary) { this.checklistSummary = checklistSummary; }

    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
}