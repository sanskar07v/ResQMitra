package com.resqmitra.module.incident.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.resqmitra.dto.ApiResponse;
import com.resqmitra.module.incident.dto.DateModel;
import com.resqmitra.module.incident.dto.IncidentRegModel;
import com.resqmitra.module.incident.dto.IncidentVolunteerRegModel;
import com.resqmitra.module.incident.dto.SearchModel;
import com.resqmitra.module.incident.entity.Incident;
import com.resqmitra.module.incident.entity.IncidentVolunteer;
import com.resqmitra.module.incident.exception.IncidentNotFoundException;
import com.resqmitra.module.incident.service.IncidentService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/incident")
@Slf4j
public class IncidentController {

	@Autowired
	private IncidentService incService;
	
	@PostMapping("/register")
	public ResponseEntity<ApiResponse> registerIncident(@RequestBody @Valid IncidentRegModel model) throws MessagingException{
		
		Incident inc = incService.registerIncident(model);
		
		if(inc==null) {
			log.warn("Incident is not created. Repeated Request");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			        .body(new ApiResponse(false, null, "Incident Not Created"));
		
		}
		
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new ApiResponse(true, null, "Incident is created"));
		
	}
	
	@PostMapping("/volunteer/register")
	@PreAuthorize("hasRole('VOLUNTEER')")
	public ResponseEntity<ApiResponse> registerIncidentVolunteer(@RequestBody @Valid IncidentVolunteerRegModel model) throws IncidentNotFoundException{
		IncidentVolunteer volunteer = incService.registerIncVolunteer(model);
		
		if(volunteer==null) {
			log.warn("Incident is not assigned to Volunteer. incident: {} , volunteer: {}" , model.getIncidentId() , model.getVolunteerId());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			        .body(new ApiResponse(false, null, "Incident is not assigned to Volunteer"));
		
		}
		
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new ApiResponse(true, null, "Incident is assigned to Volunteer"));
	}
	
	@GetMapping("/get")
	@PreAuthorize("authenticated()")
	public ResponseEntity<ApiResponse> getAllIncident(){
		List<Incident> incidents = incService.getAllIncident();
	    return ResponseEntity.ok(new ApiResponse(true , incidents , "List of Incidents")); // 200 OK
	    
	}
	
	@GetMapping("/get/byvolunteer")
	@PreAuthorize("hasRole('VOLUNTEER')")
	public ResponseEntity<ApiResponse> getIncidentByVolunteer(){
		List<Incident> incidents = incService.getIncidentByVolunteer();
		return ResponseEntity.ok(new ApiResponse(true, incidents, "List of Incidents"));
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/get/bydate")
	public ResponseEntity<ApiResponse> getIncidentByDate(@RequestBody DateModel model){
		List<Incident> incidents = incService.getIncidentByDate(model);
		return ResponseEntity.ok(new ApiResponse(true, incidents, "List of Incidents"));
	}
	
	@PreAuthorize("hasRole('VOLUNTEER')")
	@PutMapping("/resolve/{incidentId}")
	public ResponseEntity<ApiResponse> resolveIncident(@PathVariable Long incidentId){
		Incident inc = incService.getIncidentById(incidentId);
		incService.resolveIncident(inc);
		
		return ResponseEntity.noContent().build();
	}
	
	
	@PostMapping("/get/data")
	public ResponseEntity<ApiResponse> searchIncident(@RequestBody SearchModel model){
		List<Incident> incidents = incService.searchIncident(model.getStartDate() , model.getEndDate() , model.getKeyword());
		return ResponseEntity.ok(new ApiResponse(true, incidents, "List of Incidents"));

	}
}