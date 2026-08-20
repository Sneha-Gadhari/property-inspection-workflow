package com.propertyinspection.propertyinspectionworkflow;

import com.propertyinspection.propertyinspectionworkflow.model.InspectionRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InspectionRequestRepository extends JpaRepository<InspectionRequest, Long> {
}