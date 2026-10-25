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

    @Lob
    @Column(columnDefinition = "CLOB")
    private String documentPath;

    private String ownerName;
    private String preferredDate;
    private String urgency = "MEDIUM";
    private Integer numRooms;
    private Integer numFloors;
    private Double areaSqFt;
    private String checklistSummary;
    private java.time.LocalDateTime createdAt;

    // Real-world workflow extensions
    private Integer yearBuilt;
    private String inspectionOrderType;
    private String utilitiesStatus;
    private String occupancyStatus;
    private String accessInstructions;
    private String scheduledDate;
    private Double feeAmount = 3500.0;
    private String paymentStatus = "PENDING";
    private Boolean agreementSigned = false;
    private String clientSignature;
    private String inspectorSignature;
    private String inspectorLicense;
    private String overallCondition = "GOOD";
    private Integer safetyHazardsCount = 0;
    private Integer majorDefectsCount = 0;
    private Integer minorDefectsCount = 0;
    private Integer clientRating;
    private String clientFeedback;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String checklistJson;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String defectListJson;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String auditTrailJson;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = java.time.LocalDateTime.now();
        }
        if (this.urgency == null || this.urgency.isBlank()) {
            this.urgency = "MEDIUM";
        }
        if (this.paymentStatus == null) {
            this.paymentStatus = "PENDING";
        }
        if (this.overallCondition == null) {
            this.overallCondition = "GOOD";
        }
        if (this.safetyHazardsCount == null) this.safetyHazardsCount = 0;
        if (this.majorDefectsCount == null) this.majorDefectsCount = 0;
        if (this.minorDefectsCount == null) this.minorDefectsCount = 0;
        if (this.agreementSigned == null) this.agreementSigned = false;
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

    public Integer getYearBuilt() { return yearBuilt; }
    public void setYearBuilt(Integer yearBuilt) { this.yearBuilt = yearBuilt; }

    public String getInspectionOrderType() { return inspectionOrderType; }
    public void setInspectionOrderType(String inspectionOrderType) { this.inspectionOrderType = inspectionOrderType; }

    public String getUtilitiesStatus() { return utilitiesStatus; }
    public void setUtilitiesStatus(String utilitiesStatus) { this.utilitiesStatus = utilitiesStatus; }

    public String getOccupancyStatus() { return occupancyStatus; }
    public void setOccupancyStatus(String occupancyStatus) { this.occupancyStatus = occupancyStatus; }

    public String getAccessInstructions() { return accessInstructions; }
    public void setAccessInstructions(String accessInstructions) { this.accessInstructions = accessInstructions; }

    public String getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(String scheduledDate) { this.scheduledDate = scheduledDate; }

    public Double getFeeAmount() { return feeAmount; }
    public void setFeeAmount(Double feeAmount) { this.feeAmount = feeAmount; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public Boolean getAgreementSigned() { return agreementSigned; }
    public void setAgreementSigned(Boolean agreementSigned) { this.agreementSigned = agreementSigned; }

    public String getClientSignature() { return clientSignature; }
    public void setClientSignature(String clientSignature) { this.clientSignature = clientSignature; }

    public String getInspectorSignature() { return inspectorSignature; }
    public void setInspectorSignature(String inspectorSignature) { this.inspectorSignature = inspectorSignature; }

    public String getInspectorLicense() { return inspectorLicense; }
    public void setInspectorLicense(String inspectorLicense) { this.inspectorLicense = inspectorLicense; }

    public String getOverallCondition() { return overallCondition; }
    public void setOverallCondition(String overallCondition) { this.overallCondition = overallCondition; }

    public Integer getSafetyHazardsCount() { return safetyHazardsCount; }
    public void setSafetyHazardsCount(Integer safetyHazardsCount) { this.safetyHazardsCount = safetyHazardsCount; }

    public Integer getMajorDefectsCount() { return majorDefectsCount; }
    public void setMajorDefectsCount(Integer majorDefectsCount) { this.majorDefectsCount = majorDefectsCount; }

    public Integer getMinorDefectsCount() { return minorDefectsCount; }
    public void setMinorDefectsCount(Integer minorDefectsCount) { this.minorDefectsCount = minorDefectsCount; }

    public Integer getClientRating() { return clientRating; }
    public void setClientRating(Integer clientRating) { this.clientRating = clientRating; }

    public String getClientFeedback() { return clientFeedback; }
    public void setClientFeedback(String clientFeedback) { this.clientFeedback = clientFeedback; }

    public String getChecklistJson() { return checklistJson; }
    public void setChecklistJson(String checklistJson) { this.checklistJson = checklistJson; }

    public String getDefectListJson() { return defectListJson; }
    public void setDefectListJson(String defectListJson) { this.defectListJson = defectListJson; }

    public String getAuditTrailJson() { return auditTrailJson; }
    public void setAuditTrailJson(String auditTrailJson) { this.auditTrailJson = auditTrailJson; }
}