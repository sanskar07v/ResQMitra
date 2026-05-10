package com.resqmitra.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

	private String name;
	private String email;
	private String phone;
	private String role;
	private String token;
	private LocalDateTime expiryDate;
	public LoginResponse(String name, String email, String role, String token, Date expiryDate , String phone) {
		super();
		this.name = name;
		this.email = email;
		this.role = role;
		this.token = token;
		this.expiryDate = expiryDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();;
        this.phone = phone;
	}
	
	
}
