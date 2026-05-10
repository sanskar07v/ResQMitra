package com.resqmitra.module.incident.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncidentRegModel {
	
	@NotNull(message = "Latitude cannot be null")
	private Double latitude;
	
	@NotNull(message = "Longitude cannot be null")
	private Double longitude;
	
	private String description;
}
