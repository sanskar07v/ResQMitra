package com.resqmitra.module.incident.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncidentVolunteerRegModel {

	@NotNull(message = "Incident Id can not be null")
	private Long incidentId;
	
	@NotNull(message = "Volunteer Id can not be null")
	@NotBlank(message = "Volunteer Id can not be blank")
	private String volunteerId;
	
}
