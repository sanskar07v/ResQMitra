package com.resqmitra.module.user.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserUpdateModel {

	
	private String name;
	
	
	@Pattern(
        regexp = "^(\\+91)?[6-9]\\d{9}$",
        message = "Invalid phone number. Must be a valid 10-digit number, with optional +91."
    )
	private String phone;
	
}
