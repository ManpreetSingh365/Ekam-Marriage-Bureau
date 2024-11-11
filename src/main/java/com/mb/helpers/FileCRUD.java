package com.mb.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mb.entities.User;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileCRUD {

	// Check that File is of Excel Type or Not ?
	public static boolean checkExcelFormat(MultipartFile file) {

		String contentType = file.getContentType();

		if (contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
			return true;
		} else {
			return false;
		}

	}

	// Convert Excel to List of Users
	public static List<User> convertExcelToListOfUser(InputStream is) {
		List<User> list = new ArrayList<>();

		try (XSSFWorkbook workbook = new XSSFWorkbook(is)) { // Use try-with-resources
			XSSFSheet sheet = workbook.getSheetAt(0); // Get the first sheet
			DataFormatter formatter = new DataFormatter();

			// Process rows in smaller batches
			int batchSize = 500;
			List<User> batch = new ArrayList<>();

			int rowNumber = 0;
			for (Row row : sheet) {
				if (rowNumber == 0) { // Skip header row
					rowNumber++;
					continue;
				}

				User user = new User();
				boolean isValidRow = true;
				int cid = 0;

				for (Cell cell : row) {
					String cellValue = formatter.formatCellValue(cell);

					try {
						switch (cid) {
						case 0:
							user.setUserId(Long.parseLong(cellValue));
							break;
						case 1:
							user.setAddress(cellValue);
							break;
						case 2:
							// Left empty for age calculation later
							break;
						case 3:
							user.setAnyDemand(cellValue);
							break;
						case 4:
							user.setAnyRemarks(cellValue);
							break;
						case 5:
							user.setBrithTime(cellValue);
							break;
						case 6:
							user.setCaste(cellValue);
							break;
						case 7:
							// Handle Date of Birth and Age calculation
							String dateOfBirth = cellValue;
							LocalDate dob = parseDate(dateOfBirth);
							if (dob != null) {
								user.setDateOfBirth(dob.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
								user.setAge(Period.between(dob, LocalDate.now()).getYears());
							} else {
								isValidRow = false;
							}
							break;

						case 8: // Email
							if (cellValue == null || cellValue.trim().isEmpty()) {
								System.err.printf("Row %d: Email is required but missing.%n", rowNumber + 1);
								isValidRow = false; // Skip this row
//								System.out.println("\n\n" + cellValue.trim() + "\n");
							} else {
								user.setEmail(cellValue.trim());
							}
							break;

						case 9:
							user.setFamilyStatus(cellValue);
							break;
						case 10:
							user.setFatherJobSalary(cellValue.isEmpty() ? "Not Mention" : cellValue);
							break;
						case 11:
							user.setFatherJobTitle(cellValue);
							break;
						case 12:
							user.setFatherName(cellValue);
							break;
						case 13:
							user.setFatherOccupation(cellValue);
							break;
						case 14:
							user.setFormFilledBy(cellValue);
							break;
						case 15:
							user.setGender(cellValue);
							break;
						case 16:
							user.setHeight(Double.parseDouble(cellValue));
							break;
						case 17:
							user.setMarriedStatus(cellValue);
							break;
						case 18:
							user.setMaxAge(Integer.parseInt(cellValue));
							break;
						case 19:
							user.setMaxHeight(Integer.parseInt(cellValue));
							break;
						case 20:
							user.setMinAge(Integer.parseInt(cellValue));
							break;
						case 21:
							user.setMinHeight(Integer.parseInt(cellValue));
							break;
						case 22:
							user.setMotherJobSalary(cellValue.isEmpty() ? "Not Mention" : cellValue);
							break;
						case 23:
							user.setMotherJobTitle(cellValue);
							break;
						case 24:
							user.setMotherName(cellValue);
							break;
						case 25:
							user.setMotherOccupation(cellValue);
							break;
						case 26:
							user.setName(cellValue);
							break;
						case 27:
							user.setNriPlace(cellValue);
							break;
						case 28:
							user.setOccupation(cellValue);
							break;
						case 29:
							user.setPassword(cellValue);
							break;
						case 30:
							user.setPhoneNumber1(cellValue);
							break;
						case 31:
							user.setPhoneNumber2(cellValue);
							break;
						case 32:
							parseImageUrls(cellValue, user);
							break;
						case 33:
							user.setPlace(cellValue);
							break;
						case 34:
							user.setQualification(cellValue);
							break;
						case 35:
							user.setQualificationField(cellValue);
							break;
						case 36:
							user.setRazorpaySignature(cellValue);
							break;
						case 37:
							user.setReligion(cellValue);
							break;
						case 38:
							user.setSubcaste(cellValue);
							break;
						case 39:
							user.setSubscriptionIsActive(Boolean.parseBoolean(cellValue));
							break;
						case 40:
							user.setTotalBrothers(cellValue.isEmpty() ? 0 : Integer.parseInt(cellValue));
							break;
						case 41:
							user.setTotalFamilyMembers(cellValue.isEmpty() ? 0 : Integer.parseInt(cellValue));
							break;
						case 42:
							user.setTotalSisters(cellValue.isEmpty() ? 0 : Integer.parseInt(cellValue));
							break;
						case 43:
							user.setYourJobSalary(cellValue.isEmpty() ? "Not Mention" : cellValue);
							break;
						case 44:
							user.setYourJobTitle(cellValue);
							break;
						default:
							break;
						}
					} catch (NumberFormatException nfe) {
						System.err.printf("Row %d, Column %d: Invalid number format for value '%s' - %s%n",
								rowNumber + 1, cid + 1, cellValue, nfe.getMessage());
						isValidRow = false;
					} catch (DateTimeParseException dtpe) {
						System.err.printf("Row %d, Column %d: Invalid date format for value '%s' - %s%n", rowNumber + 1,
								cid + 1, cellValue, dtpe.getMessage());
						isValidRow = false;
					} catch (Exception e) {
						System.err.printf("Row %d, Column %d: Error processing value '%s' - %s%n", rowNumber + 1,
								cid + 1, cellValue, e.getMessage());
						isValidRow = false;
					}
					cid++;
				}

				// Only add the user if the row is valid and email is provided
				if (isValidRow && user.getEmail().trim() != null && !user.getEmail().trim().isEmpty()) {
					batch.add(user);

					// If the batch size is reached, process it
					if (batch.size() >= batchSize) {
						list.addAll(batch);
						batch.clear(); // Clear the batch to free up memory
					}
				} else {

					System.err.printf("Row %d: Skipped due to missing critical data.%n", rowNumber + 1);
					// isValidRow: false | user.getEmail(): null | isValidRow: false
					// System.err.printf("isValidRow: " + isValidRow + " | user.getEmail(): " +
					// user.getEmail() + " | isValidRow: " + isValidRow + "\n");
				}
				rowNumber++;
			}

			// Add any remaining users in the last batch
			if (!batch.isEmpty()) {
				list.addAll(batch);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	// Helper method to parse date
	private static LocalDate parseDate(String dateString) {
		DateTimeFormatter dateTimeFormatter1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter dateTimeFormatter2 = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		LocalDate dob = null;

		try {
			dob = LocalDate.parse(dateString, dateTimeFormatter1);
		} catch (DateTimeParseException e) {
			try {
				dob = LocalDate.parse(dateString, dateTimeFormatter2);
			} catch (DateTimeParseException ignored) {
				System.err.printf("Invalid date format for value '%s'.%n", dateString);
			}
		}
		return dob;
	}

	// Helper method to parse image URLs
	private static void parseImageUrls(String cellValue, User user) {
		if (cellValue.startsWith("[") && cellValue.endsWith("]")) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				List<String> imageUrls = objectMapper.readValue(cellValue, new TypeReference<List<String>>() {
				});
				user.setImagesList(imageUrls); // Set the parsed list directly
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				// In case of error, fallback to treat it as a plain string
				List<String> imageUrls = new ArrayList<>();
				imageUrls.add(cellValue);
				user.setImagesList(imageUrls);
			}
		} else if (cellValue.contains(",")) {
			// If multiple URLs are separated by commas, split and add them to the list
			List<String> imageUrls = Arrays.asList(cellValue.split(","));
			user.setImagesList(imageUrls);
		} else {
			// If it's a single URL, create a list with that single URL
			user.setImagesList(new ArrayList<>(Arrays.asList(cellValue)));
		}
	}
}
