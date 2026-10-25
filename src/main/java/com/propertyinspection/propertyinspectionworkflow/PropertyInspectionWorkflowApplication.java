package com.propertyinspection.propertyinspectionworkflow;

import com.propertyinspection.propertyinspectionworkflow.model.InspectionRequest;
import com.propertyinspection.propertyinspectionworkflow.model.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class PropertyInspectionWorkflowApplication {

	public static void main(String[] args) {
		SpringApplication.run(PropertyInspectionWorkflowApplication.class, args);
	}

	@Bean
	public CommandLineRunner seedInitialData(UserRepository userRepository, InspectionRequestRepository inspectionRepo) {
		return args -> {
			// 1. Seed Demo Admin
			if (userRepository.findByUsername("demo").isEmpty()) {
				User admin = new User();
				admin.setUsername("demo");
				admin.setPassword("demo123");
				admin.setRole(User.Role.ADMIN);
				admin.setApproved(true);
				admin.setFullName("DevOps System Administrator");
				admin.setPhone("+91 98000 12345");
				admin.setDob("1988-04-12");
				admin.setAge(38);
				admin.setAddress("Admin IT Wing, Pune Municipal Area, Pune");
				userRepository.save(admin);
			}

			// 2. Seed Indian Field Inspector
			if (userRepository.findByUsername("rahul_inspector").isEmpty()) {
				User inspector = new User();
				inspector.setUsername("rahul_inspector");
				inspector.setPassword("pass123");
				inspector.setRole(User.Role.INSPECTOR);
				inspector.setApproved(true);
				inspector.setFullName("Rahul Verma");
				inspector.setPhone("+91 98220 54321");
				inspector.setDob("1992-06-25");
				inspector.setAge(34);
				inspector.setLicenseNumber("INSP-MH-2022-8819");
				inspector.setCertifications("InterNACHI Certified Master Inspector, PMC Structural Safety Auditor");
				inspector.setAddress("Kothrud, Pune, Maharashtra");
				userRepository.save(inspector);
			}

			// 3. Seed Indian Senior Reviewer
			if (userRepository.findByUsername("priya_reviewer").isEmpty()) {
				User reviewer = new User();
				reviewer.setUsername("priya_reviewer");
				reviewer.setPassword("pass123");
				reviewer.setRole(User.Role.REVIEWER);
				reviewer.setApproved(true);
				reviewer.setFullName("Priya Deshmukh (Reviewer)");
				reviewer.setPhone("+91 98901 22334");
				reviewer.setDob("1986-09-14");
				reviewer.setAge(40);
				reviewer.setCertifications("Senior Municipal Review Officer, ASHI Associate Member");
				reviewer.setAddress("Shivajinagar, Pune, Maharashtra");
				userRepository.save(reviewer);
			}

			// 4. Seed Indian Property Owner
			if (userRepository.findByUsername("sneha_owner").isEmpty()) {
				User owner = new User();
				owner.setUsername("sneha_owner");
				owner.setPassword("pass123");
				owner.setRole(User.Role.PROPERTY_OWNER);
				owner.setApproved(true);
				owner.setEmail("snehag1795@gmail.com");
				owner.setFullName("Sneha Gadhari");
				owner.setPhone("+91 98220 99887");
				owner.setDob("1995-07-17");
				owner.setAge(31);
				owner.setAddress("Amanora Gateway Towers, Hadapsar, Pune");
				userRepository.save(owner);
			}

			// 5. Seed Rich Indian Dummy Inspection Data for Analytics & Live Demos
			if (inspectionRepo.count() == 0) {
				LocalDateTime now = LocalDateTime.now();

				// Dummy 1: Approved Residential - Pune
				inspectionRepo.save(createInspection(
						"Bungalow 14, Koregaon Park, Lane 5, Pune, Maharashtra",
						"rahul_inspector",
						"Rajesh & Sunita Sharma",
						"Residential",
						"+91 98220 12345",
						"LOW", 6, 2, 3400.0, "2026-10-20",
						"Foundation: Passed | Structural Beams: Sound | Electrical Wiring: Tested | Rainwater Harvesting: Installed",
						"Independent 2-storey luxury bungalow. Structural stability and solar rooftop inspection requested.",
						"Full compliance with PMC municipal building codes. Structural fitness certificate approved.",
						InspectionRequest.Status.APPROVED,
						now.minusDays(3)
				));

				// Dummy 2: Under Review Commercial - Cyber City IT Park
				inspectionRepo.save(createInspection(
						"Unit 804, Tower B, Cyber City IT Park, Magarpatta, Pune",
						"rahul_inspector",
						"Vikram Malhotra (Apex Tech Spaces)",
						"Commercial",
						"+91 98901 67890",
						"HIGH", 24, 3, 18500.0, "2026-10-21",
						"Fire Sprinklers: Verified | Emergency Exit Signs: Functional | Server Room Gas Suppression: Pending test report",
						"Commercial office floor lease renewal. Detailed fire exit routes and HVAC ventilation audit required.",
						"Fire NOC verified. Awaiting secondary HVAC damper pressure calibration report from facility team.",
						InspectionRequest.Status.UNDER_REVIEW,
						now.minusDays(2)
				));

				// Dummy 3: Submitted Residential - Hinjewadi (Owned by Sneha Gadhari / sneha_owner)
				inspectionRepo.save(createInspection(
						"Flat 1202, Royal Palms Residency, Hinjewadi Phase 1, Pune",
						"rahul_inspector",
						"Sneha Gadhari (sneha_owner)",
						"Residential",
						"+91 98220 99887",
						"MEDIUM", 4, 1, 1650.0, "2026-10-24",
						"Plumbing & Drainage: Clear | Balcony Railings: Sturdy | MCB Distribution Box: All phases active",
						"3BHK high-rise apartment pre-possession audit. Balcony railing height and electrical earth leakage check.",
						null,
						InspectionRequest.Status.SUBMITTED,
						now.minusDays(1)
				));

				// Dummy 4: Rejected Commercial - Mall
				inspectionRepo.save(createInspection(
						"Shop No. 5 & 6, Phoenix Galleria Mall, Viman Nagar, Pune",
						"rahul_inspector",
						"Aarav Patel (Patel Retail Ventures)",
						"Commercial",
						"+91 99231 45678",
						"CRITICAL", 8, 2, 4200.0, "2026-10-18",
						"Fire Alarms: Non-compliant | Emergency Lighting: Faulty | Structural: Acceptable",
						"Retail clothing store expansion and mezzanine floor structural load verification.",
						"Unauthorized steel mezzanine framing without structural engineer sign-off. Blocked secondary fire corridor. Remediation mandatory before re-inspection.",
						InspectionRequest.Status.REJECTED,
						now.minusDays(2)
				));

				// Dummy 5: Submitted Industrial - MIDC
				inspectionRepo.save(createInspection(
						"Plot C-42, MIDC Industrial Area, Bhosari, Pimpri-Chinchwad",
						"rahul_inspector",
						"Anand Kulkarni (Kulkarni Auto Precision)",
						"Industrial",
						"+91 98450 78123",
						"HIGH", 12, 2, 26000.0, "2026-10-25",
						"Overhead Crane Gantry: Tested 10T | Industrial Exhaust: Dual ducts active | Hazardous Waste Storage: Secondary containment intact",
						"Automotive component manufacturing shed. Overhead crane gantry rail alignment and industrial effluent sump inspection.",
						null,
						InspectionRequest.Status.SUBMITTED,
						now.minusDays(1)
				));

				// Dummy 6: Approved Industrial - Chakan Logistics
				inspectionRepo.save(createInspection(
						"Shed 19, Logistics Park, Chakan Industrial Corridor, Pune",
						"rahul_inspector",
						"Suresh Nair (Deccan Supply Chain Solutions)",
						"Industrial",
						"+91 97312 89012",
						"MEDIUM", 6, 1, 35000.0, "2026-10-19",
						"Loading Docks: Sound | Structural: Reinforced concrete | Drainage: Passed",
						"FMCG dry logistics warehouse. Heavy-duty floor load capacity and lightning arrestor inspection.",
						"High-grade industrial flooring certified for 8-ton axle load. Lightning protection and earthing pits verified within permissible limits.",
						InspectionRequest.Status.APPROVED,
						now
				));

				// Dummy 7: Approved Residential - Hadapsar (Owned by Sneha Gadhari / sneha_owner)
				inspectionRepo.save(createInspection(
						"Penthouse 1801, Amanora Gateway Towers, Hadapsar, Pune",
						"rahul_inspector",
						"Sneha Gadhari (sneha_owner)",
						"Residential",
						"+91 98220 99887",
						"LOW", 5, 2, 2850.0, "2026-10-22",
						"Waterproofing: Intact | Terrace Railings: Tempered glass certified | Fire Sensor Nodes: Tested",
						"Duplex penthouse pre-handover audit. Terrace garden waterproofing and electrical load balance check.",
						"Terrace load and safety barrier heights compliant with PMC norms. Occupancy inspection approved.",
						InspectionRequest.Status.APPROVED,
						now.minusDays(1)
				));
			}
		};
	}

	private InspectionRequest createInspection(
			String address, String inspector, String owner, String type, String contact,
			String urgency, int rooms, int floors, double area, String date,
			String checklist, String notes, String reviewerRemarks,
			InspectionRequest.Status status, LocalDateTime createdAt) {
		InspectionRequest req = new InspectionRequest();
		req.setPropertyAddress(address);
		req.setInspectorName(inspector);
		req.setOwnerName(owner);
		req.setPropertyType(type);
		req.setContactNumber(contact);
		req.setUrgency(urgency);
		req.setNumRooms(rooms);
		req.setNumFloors(floors);
		req.setAreaSqFt(area);
		req.setPreferredDate(date);
		req.setChecklistSummary(checklist);
		req.setNotes(notes);
		req.setReviewerRemarks(reviewerRemarks);
		req.setStatus(status);
		req.setCreatedAt(createdAt);

		// Initialize realistic enterprise fields
		req.setYearBuilt(2018);
		req.setInspectionOrderType(type != null ? type : "Residential");
		req.setUtilitiesStatus("Power & Water Active");
		req.setOccupancyStatus("Occupied");
		req.setAccessInstructions("Lockbox code 4291 at security gate");
		req.setScheduledDate(date);
		req.setFeeAmount(3500.0);
		req.setPaymentStatus(status == InspectionRequest.Status.APPROVED ? "PAID" : "PENDING");
		req.setAgreementSigned(true);
		req.setOverallCondition(status == InspectionRequest.Status.REJECTED ? "POOR" : (status == InspectionRequest.Status.APPROVED ? "EXCELLENT" : "GOOD"));
		req.setInspectorLicense("INSP-MH-2022-8819");
		req.setSafetyHazardsCount(status == InspectionRequest.Status.REJECTED ? 2 : 0);
		req.setMajorDefectsCount(status == InspectionRequest.Status.REJECTED ? 3 : 0);
		req.setMinorDefectsCount(status == InspectionRequest.Status.APPROVED ? 1 : (status == InspectionRequest.Status.REJECTED ? 4 : 2));

		if (status == InspectionRequest.Status.REJECTED) {
			req.setDefectListJson("[{\"category\":\"Fire Safety\",\"desc\":\"Blocked secondary fire corridor & missing emergency exit signage\",\"severity\":\"Safety Hazard\",\"cost\":18000,\"recommendation\":\"Repair\",\"specialist\":\"Fire Safety Officer\"},{\"category\":\"Structure\",\"desc\":\"Unauthorized steel mezzanine without structural load sign-off\",\"severity\":\"Major\",\"cost\":45000,\"recommendation\":\"Specialist Evaluation\",\"specialist\":\"Structural Engineer\"}]");
		} else {
			req.setDefectListJson("[{\"category\":\"Plumbing\",\"desc\":\"Minor washer drip in master bath fixture\",\"severity\":\"Minor\",\"cost\":1200,\"recommendation\":\"Repair\",\"specialist\":\"Plumber\"}]");
		}

		String dtStr = createdAt.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		req.setAuditTrailJson("[{\"time\":\"" + dtStr + "\",\"action\":\"Order Submitted\",\"by\":\"" + owner + "\",\"detail\":\"Intake order logged\"}," +
				"{\"time\":\"" + dtStr + "\",\"action\":\"Review & Assignment\",\"by\":\"Reviewer\",\"detail\":\"Assigned to " + inspector + "\"}," +
				"{\"time\":\"" + dtStr + "\",\"action\":\"Current State\",\"by\":\"System\",\"detail\":\"Status: " + status + "\"}]");

		return req;
	}
}