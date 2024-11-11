package com.mb.controller;

import com.mb.entities.User;
import com.mb.helpers.FileCRUD;
import com.mb.helpers.FileMultipartFile;
import com.mb.helpers.Helper;
import com.mb.services.UserService;
import com.mb.services.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RestController
//@CrossOrigin("*")
public class UsersAPIController {

	@Autowired
	private UserService userService;

	@Value("${admin.email}")
	private String adminEmail;

//	@PostMapping("/userfile/upload")
//	public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
//		if (file.isEmpty()) {
//			return ResponseEntity.badRequest().body(Map.of("message", "File is empty!"));
//		}
//
//		// Validate file format (e.g., Excel)
//		if (!FileCRUD.checkExcelFormat(file)) {
//			return ResponseEntity.badRequest().body(Map.of("message", "Please upload a valid Excel file!"));
//		}
//
//		// Process the file directly
//		// serService.saveFile(file); // Assuming saveFile accepts MultipartFile
//
//// Process the file in a separate thread to avoid blocking ----->
//		CompletableFuture.runAsync(() -> {
//			// Process the file directly
//			userService.saveFile(file); // Assuming saveFile accepts MultipartFile
//			// If additional processing is needed, it can be handled here
//			// For example, parsing the Excel file
//		});
//
//		return ResponseEntity
//				.ok(Map.of("message", "File is uploaded and is being processed. You will be notified on completion."));
//	}

	private final Map<String, String> processingStatus = new ConcurrentHashMap<>(); // Map to store processing status

	@PostMapping("/userfile/upload")
	public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
		if (file.isEmpty()) {
			return ResponseEntity.badRequest().body(Map.of("message", "File is empty!"));
		}

		// Validate file format (e.g., Excel)
		if (!FileCRUD.checkExcelFormat(file)) {
			return ResponseEntity.badRequest().body(Map.of("message", "Please upload a valid Excel file!"));
		}

		String processingId = generateProcessingId(); // Generate a unique ID for the processing task
		processingStatus.put(processingId, "Processing"); // Set initial status

		// Process the file in a separate thread to avoid blocking
		CompletableFuture.runAsync(() -> {
			userService.saveFile(file); // Process the file
			processingStatus.put(processingId, "Completed"); // Update status to completed
		});

		return ResponseEntity
				.ok(Map.of("message", "File is uploaded and is being processed.", "processingId", processingId));
	}

	@GetMapping("/userfile/status")
	public ResponseEntity<Map<String, String>> getStatus(@RequestParam String processingId) {
		String status = processingStatus.get(processingId);
		if (status == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(Map.of("status", status));
	}

	private String generateProcessingId() {
		// Implement a unique ID generation logic
		return String.valueOf(System.currentTimeMillis());
	}

	@GetMapping("/getalluser")
	public ResponseEntity<?> getAllUsers(Authentication authentication) {
		// Illegal Access Handler
		String username = Helper.getEmailOfLoggedInUser(authentication);
		User userData = userService.getUserByEmail(username);

		// Check if the user is authorized
		if (!userData.getEmail().equals(adminEmail)) {
			// Return a redirect response for web applications
			return ResponseEntity.status(HttpStatus.FOUND) // 302 Found
					.header("Location", "/notauthorizedaccess").build();
		}

		List<User> users = this.userService.getAllUsers();
		return ResponseEntity.ok(users);
	}

	@GetMapping("/export/users")
	public ResponseEntity<byte[]> exportUsersToExcel() {
		List<User> users = userService.getAllUsers();

//		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
		try (SXSSFWorkbook workbook = new SXSSFWorkbook();
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			workbook.setCompressTempFiles(true); // Optional GZIP compression for temp files

			Sheet sheet = workbook.createSheet("Users");

			// Create header row
			Row headerRow = sheet.createRow(0);
			String[] headers = { "user_id", "address", "age", "any_demand", "any_remarks", "brith_time", "caste",
					"date_of_birth", "email", "family_status", "father_job_salary", "father_job_title", "father_name",
					"father_occupation", "form_filled_by", "gender", "height", "married_status", "max_age",
					"max_height", "min_age", "min_height", "mother_job_salary", "mother_job_title", "mother_name",
					"mother_occupation", "name", "nri_place", "occupation", "password", "phone_number1",
					"phone_number2", "images", "place", "qualification", "qualification_field", "razorpay_signature",
					"religion", "subcaste", "subscription_is_active", "total_brothers", "total_family_members",
					"total_sisters", "your_job_salary", "your_job_title" };

			for (int i = 0; i < headers.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(headers[i]);
			}

			// Populate data
			int rowNum = 1;
			for (User user : users) {
				Row row = sheet.createRow(rowNum++);
				row.createCell(0).setCellValue(user.getUserId() != null ? user.getUserId() : 0);
				row.createCell(1).setCellValue(user.getAddress() != null ? user.getAddress() : "Not Mention");
				row.createCell(2).setCellValue(user.getAge() != null ? user.getAge() : 0);
				row.createCell(3).setCellValue(user.getAnyDemand() != null ? user.getAnyDemand() : "Not Mention");
				row.createCell(4).setCellValue(user.getAnyRemarks() != null ? user.getAnyRemarks() : "Not Mention");
				row.createCell(5).setCellValue(user.getBrithTime() != null ? user.getBrithTime() : "Not Mention");
				row.createCell(6).setCellValue(user.getCaste() != null ? user.getCaste() : "Not Mention");
				row.createCell(7).setCellValue(user.getDateOfBirth() != null ? user.getDateOfBirth() : "Not Mention");
				row.createCell(8).setCellValue(user.getEmail() != null ? user.getEmail() : "Not Mention");
				row.createCell(9).setCellValue(user.getFamilyStatus() != null ? user.getFamilyStatus() : "Not Mention");
				row.createCell(10)
						.setCellValue(user.getFatherJobSalary() != null ? user.getFatherJobSalary() : "Not Mention");
				row.createCell(11)
						.setCellValue(user.getFatherJobTitle() != null ? user.getFatherJobTitle() : "Not Mention");
				row.createCell(12).setCellValue(user.getFatherName() != null ? user.getFatherName() : "Not Mention");
				row.createCell(13)
						.setCellValue(user.getFatherOccupation() != null ? user.getFatherOccupation() : "Not Mention");
				row.createCell(14)
						.setCellValue(user.getFormFilledBy() != null ? user.getFormFilledBy() : "Not Mention");
				row.createCell(15).setCellValue(user.getGender() != null ? user.getGender() : "Not Mention");
				row.createCell(16).setCellValue(user.getHeight() != null ? user.getHeight() : 0);
				row.createCell(17)
						.setCellValue(user.getMarriedStatus() != null ? user.getMarriedStatus() : "Not Mention");
				row.createCell(18).setCellValue(user.getMaxAge() != 0 ? user.getMaxAge() : 0);
				row.createCell(19).setCellValue(user.getMaxHeight() != 0 ? user.getMaxHeight() : 0);
				row.createCell(20).setCellValue(user.getMinAge() != 0 ? user.getMinAge() : 0);
				row.createCell(21).setCellValue(user.getMinHeight() != 0 ? user.getMinHeight() : 0);
				row.createCell(22)
						.setCellValue(user.getMotherJobSalary() != null ? user.getMotherJobSalary() : "Not Mention");
				row.createCell(23)
						.setCellValue(user.getMotherJobTitle() != null ? user.getMotherJobTitle() : "Not Mention");
				row.createCell(24).setCellValue(user.getMotherName() != null ? user.getMotherName() : "Not Mention");
				row.createCell(25)
						.setCellValue(user.getMotherOccupation() != null ? user.getMotherOccupation() : "Not Mention");
				row.createCell(26).setCellValue(user.getName() != null ? user.getName() : "Not Mention");
				row.createCell(27).setCellValue(user.getNriPlace() != null ? user.getNriPlace() : "Not Mention");
				row.createCell(28).setCellValue(user.getOccupation() != null ? user.getOccupation() : "Not Mention");
				row.createCell(29).setCellValue(user.getPassword() != null ? user.getPassword() : "Not Mention");
				row.createCell(30)
						.setCellValue(user.getPhoneNumber1() != null ? user.getPhoneNumber1() : "Not Mention");
				row.createCell(31)
						.setCellValue(user.getPhoneNumber2() != null ? user.getPhoneNumber2() : "Not Mention");
				row.createCell(32).setCellValue(user.getImagesList() != null ? user.getImages()
						: "https://res.cloudinary.com/dnhvlqc1n/image/upload/v1726864762/Image-removebg_s6ngqu.png");
				row.createCell(33).setCellValue(user.getPlace() != null ? user.getPlace() : "Not Mention");
				row.createCell(34)
						.setCellValue(user.getQualification() != null ? user.getQualification() : "Not Mention");
				row.createCell(35).setCellValue(
						user.getQualificationField() != null ? user.getQualificationField() : "Not Mention");
				row.createCell(36)
						.setCellValue(user.getRazorpaySignature() != null ? user.getRazorpaySignature() : "NULL");
				row.createCell(37).setCellValue(user.getReligion() != null ? user.getReligion() : "Not Mention");
				row.createCell(38).setCellValue(user.getSubcaste() != null ? user.getSubcaste() : "Not Mention");
				row.createCell(39).setCellValue(user.isSubscriptionIsActive());
				row.createCell(40).setCellValue(user.getTotalBrothers() != 0 ? user.getTotalBrothers() : 0);
				row.createCell(41).setCellValue(user.getTotalFamilyMembers() != 0 ? user.getTotalFamilyMembers() : 0);
				row.createCell(42).setCellValue(user.getTotalSisters() != 0 ? user.getTotalSisters() : 0);
				row.createCell(43)
						.setCellValue(user.getYourJobSalary() != null ? user.getYourJobSalary() : "Not Mention");
				row.createCell(44)
						.setCellValue(user.getYourJobTitle() != null ? user.getYourJobTitle() : "Not Mention");
			}

			workbook.write(outputStream);
			// Assuming outputStream has been previously defined
			byte[] byteArray = outputStream.toByteArray();

			// Get current date and time for filename
			String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
			String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH-mm-ss"));
			String filename = "MBusers_" + date + "_" + time + ".xlsx";

			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
			httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

			return ResponseEntity.ok().headers(httpHeaders).body(byteArray);

		} catch (Exception e) {
			e.printStackTrace(); // Log the error
			return ResponseEntity.internalServerError().body(null);
		}
	}

	private CellStyle createHeaderStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBold(true);
		style.setFont(font);
		return style;
	}

}
