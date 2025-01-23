package com.mb.validators;

import java.util.List;

import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

import com.mb.forms.UserFormDetails;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

	// let's define... type, height, width
	private static final long MAX_FILE_SIZE = 1024 * 1024 * 10; // 2MB

	@Override
	public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {

		if (file == null || file.isEmpty()) {
			 context.disableDefaultConstraintViolation();
			 context.buildConstraintViolationWithTemplate("File Cannot be Empty").addConstraintViolation();
			return false;
		}

		// File Size
		System.out.println("file size: " + file.getSize());
		if (file.getSize() > MAX_FILE_SIZE) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("File size should be less than 10MB").addConstraintViolation();
			return false;
		}

		// resolution
		// try {
		// BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
		// if(bufferedImage.getHe)
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		return true;
	}
//	
//	public boolean supports(Class<?> clazz) {
//        return UserFormDetails.class.equals(clazz);
//    }
//
//
//    public void validate(Object target, Errors errors) {
//        MultipartFile userImage = (MultipartFile) target;
//
//        if (userImage.getSize() > 1024 * 1024 * 10) { // 10MB
//            errors.rejectValue("userImages", "image.size", "Image size is too large. Maximum allowed size is 5MB.");
//        }
//
//        String contentType = userImage.getContentType();
//        if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
//            errors.rejectValue("userImages", "image.type", "Only JPEG and PNG images are allowed.");
//        }
//    }
}