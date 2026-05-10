package com.resqmitra.module.notify.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

import com.resqmitra.module.incident.entity.Incident;
import com.resqmitra.module.notify.dto.IncidentNotifyModel;
import com.resqmitra.module.user.entity.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private SpringTemplateEngine templateEngine;
	
	@Async
	public void sendIncidentMail(IncidentNotifyModel model) throws MessagingException {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			
			helper.setTo(model.getTo());
			helper.setSubject("🚨 Urgent: Nearby Emergency Incident");
			
			Context context = new Context();
			
			context.setVariable("volunteerName", model.getVolunteerName());
			context.setVariable("incidentLocation", model.getIncidentLocation());
			context.setVariable("incidentTime", model.getIncidentTime());
			context.setVariable("acceptLink", model.getAcceptLink());
			context.setVariable("mapLink", model.getMapLink());
			
			String htmlContent = templateEngine.process("volunteer-request" , context);
			
			helper.setText(htmlContent , true);
			mailSender.send(message);
		}catch(MessagingException ex) {
			throw ex;
		}
	}
	
	
	// Convenience method to send to multiple volunteers
    public void sendIncidentEmailToVolunteers(List<User> volunteers, Incident incident) throws MessagingException {
        for (User volunteer : volunteers) {
        	IncidentNotifyModel model = IncidentNotifyModel.builder()
        			.to(volunteer.getEmail())
        			.volunteerName(volunteer.getName())
        			.incidentLocation(generateLocation(incident))
        			.incidentTime(incident.getCreatedAt().toString())
        			.acceptLink(generateAcceptLink(incident.getIncidentId(), volunteer.getEmail() , LocalDate.now(ZoneId.of("Asia/Kolkata")) , incident.getLatitude() , incident.getLongitude() ))
        			.build();
        	sendIncidentMail(model);
        }
    }

    private String generateAcceptLink(Long incidentId, String volunteerId, LocalDate date, Double lat, Double lng) {
        return "http://localhost:5173/volunteer/incident/alert?email="+volunteerId+"&incidentId="+incidentId+"&date="+date.format(DateTimeFormatter.ofPattern("MM-dd-yy"))+"&lat="+Double.toString(lat)+"&long="+Double.toString(lng);
    }

    private String generateLocation(Incident inc) {
    	int zoomLevel = 15; // Set a default zoom level (e.g., 15 for street level)

        String googleMapsUrl = String.format(
            "https://www.google.com/maps/search/?api=1&query=%s,%s&zoom=%d", 
            inc.getLatitude(), 
            inc.getLongitude(), 
            zoomLevel
        );

        return googleMapsUrl;
    }
}
