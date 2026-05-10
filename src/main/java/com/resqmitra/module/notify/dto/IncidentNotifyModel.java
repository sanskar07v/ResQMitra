package com.resqmitra.module.notify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class IncidentNotifyModel {
	
	private String to;
	
	private String volunteerName;
	
	private String incidentLocation;
	
	private String incidentTime;
	
	private String acceptLink;
	
	private String mapLink;
	
}
