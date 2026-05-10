package com.resqmitra.module.incident.exception;

public class IncidentNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -6611044919122772259L;
	
	public IncidentNotFoundException() {
		super("Incident not found");
	}

	public IncidentNotFoundException(Long id) {
		super("Incident with id " + id+ " is not found");
	}

}
