package com.mb.forms;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.mb.entities.User;
import com.mb.validators.ValidFile;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserFormDetailsAdmin {

	// Created Annotation that will manage File Properties such as Validate, Size,
	// Resolution...
	@ValidFile(message = "Invalid File")
	private MultipartFile userImages;

//    @NotBlank(message = "Please Check Term & Condition")
	private String agreement;

//	@Getter(value = AccessLevel.NONE)
	private boolean enabled = true;
}
