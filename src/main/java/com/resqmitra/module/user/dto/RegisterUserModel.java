package com.resqmitra.module.user.dto;

import com.resqmitra.utilities.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserModel {
	
	@NotNull(message = "Email Id cannot be null")
	@NotBlank(message = "Email Id cannot be blank")
	@Email(message = "Email Id is not in proper format")
	private String email;
	
	@NotNull(message = "Password cannot be null")
	@NotBlank(message = "Password cannot be blank")
	@Pattern(
		    regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$",
		    message = "Password must be at least 8 characters and include a digit, lowercase, uppercase, and special character"
		)
	private String password;
	
	@NotNull(message = "Name can not be null")
	@NotBlank(message = "Name can not be blank")
	private String name;
	
	@NotNull(message = "Phone Number can not be null")
	@NotBlank(message = "Phone Number can not be blank")
	@Pattern(
	        regexp = "^(\\+91)?[6-9]\\d{9}$",
	        message = "Invalid phone number. Must be a valid 10-digit number, with optional +91."
	    )
	private String phoneNum;
	
	@NotNull(message = "Role can not be null")
	private Role role;
	
	private Double latitude;
	
	private Double longitude;
}
