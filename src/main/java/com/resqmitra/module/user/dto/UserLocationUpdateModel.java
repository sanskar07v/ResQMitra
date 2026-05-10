package com.resqmitra.module.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserLocationUpdateModel {

	@NotNull
	private Double latitude;
	
	@NotNull
	private Double longitude;
}
