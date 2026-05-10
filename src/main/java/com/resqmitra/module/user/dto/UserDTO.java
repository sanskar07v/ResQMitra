package com.resqmitra.module.user.dto;

import com.resqmitra.utilities.Role;
import com.resqmitra.utilities.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserDTO {

	private String email;
	
	private String name;
	
	private String phone;
	
	private Role role;
	
	private UserStatus status;
	
	private Double latitude;
	
	private Double longitude;
	
	
}
