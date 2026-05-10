package com.resqmitra.module.incident.service;

import java.time.LocalDate;
import java.util.List;

import com.resqmitra.module.incident.dto.DateModel;
import com.resqmitra.module.incident.dto.IncidentRegModel;
import com.resqmitra.module.incident.dto.IncidentVolunteerRegModel;
import com.resqmitra.module.incident.entity.Incident;
import com.resqmitra.module.incident.entity.IncidentVolunteer;
import com.resqmitra.module.incident.exception.IncidentNotFoundException;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

public interface IncidentService {

	Incident registerIncident(@Valid IncidentRegModel model) throws MessagingException;

	IncidentVolunteer registerIncVolunteer(@Valid IncidentVolunteerRegModel model) throws IncidentNotFoundException;

	Incident getIncidentById(Long incidentId) throws IncidentNotFoundException ;
	
	List<Incident> getAllIncident();

	List<Incident> getIncidentByVolunteer();

	List<Incident> getIncidentByDate(DateModel model);

	void resolveIncident(Incident inc);

//	List<Incident> searchIncident(SearchModel model);

	List<Incident> searchIncident(LocalDate startDate, LocalDate endDate, String keyword);
}
