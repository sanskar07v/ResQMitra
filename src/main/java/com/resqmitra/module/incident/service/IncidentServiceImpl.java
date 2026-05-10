package com.resqmitra.module.incident.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.resqmitra.module.auth.exception.UnauthorizedUserException;
import com.resqmitra.module.incident.dto.DateModel;
import com.resqmitra.module.incident.dto.IncidentRegModel;
import com.resqmitra.module.incident.dto.IncidentVolunteerRegModel;
import com.resqmitra.module.incident.entity.Incident;
import com.resqmitra.module.incident.entity.IncidentVolunteer;
import com.resqmitra.module.incident.exception.IncidentNotFoundException;
import com.resqmitra.module.incident.repo.IncidentRepo;
import com.resqmitra.module.incident.repo.IncidentVolunteerRepo;
import com.resqmitra.module.incident.specification.IncidentSpecification;
import com.resqmitra.module.notify.service.EmailService;
import com.resqmitra.module.user.entity.User;
import com.resqmitra.module.user.service.UserService;
import com.resqmitra.utilities.Role;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IncidentServiceImpl implements IncidentService {

	@Autowired
	private UserService userService;

	@Autowired
	private IncidentRepo incRepo;

	@Autowired
	private IncidentVolunteerRepo incVolunteerRepo;

	@Autowired
	private EmailService emailService;

	@Override
	public Incident registerIncident(@Valid IncidentRegModel model) throws MessagingException {
		Incident inc = null;
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null && auth.getPrincipal() instanceof User user) {

				LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
				LocalDateTime twentyMinutesAgo = now.minusMinutes(20);

				// Get the latest incident created in the last 20 minutes
				Optional<Incident> recentIncident = incRepo
						.findTopByCreatedByAndCreatedAtBetweenOrderByCreatedAtDesc(user, twentyMinutesAgo, now);

				if (recentIncident.isPresent()) {
					return null;
				}

				inc = Incident.builder().latitude(model.getLatitude()).longitude(model.getLongitude()).createdBy(user)
						.description(model.getDescription()).build();

				incRepo.save(inc);

				List<User> users = userService.getNearByVolunteer(inc);
				emailService.sendIncidentEmailToVolunteers(users, inc);

				return inc;
			}
			throw new UnauthorizedUserException("User is unauthenticated or not valid");
		} catch (Exception ex) {
			if (inc != null)
				incRepo.delete(inc);
			throw ex;
		} finally {

		}
	}

	@Override
	public Incident getIncidentById(Long incidentId) throws IncidentNotFoundException {
		Optional<Incident> incOp = incRepo.findById(incidentId);

		if (incOp.isEmpty())
			throw new IncidentNotFoundException(incidentId);
		return incOp.get();
	}

	@Override
	public IncidentVolunteer registerIncVolunteer(@Valid IncidentVolunteerRegModel model)
			throws IncidentNotFoundException {

		Incident inc = this.getIncidentById(model.getIncidentId());

		if (inc == null) {
			log.warn("Incident with given id not found: {}", model.getIncidentId());
			throw new IncidentNotFoundException(model.getIncidentId());
		}
		List<IncidentVolunteer> incVols = incVolunteerRepo.findByIncident(inc);
		if(incVols==null || incVols.size()>0) {
			throw new RuntimeException("Already another volunteer is assigned");
		}
		User user = userService.getUserByIdAndRole(model.getVolunteerId(), Role.ROLE_VOLUNTEER);

		if (user == null) {
			log.warn("User with given id not found: {}", model.getVolunteerId());
			throw new UsernameNotFoundException("User with id " + model.getVolunteerId() + " not found");
		}

		IncidentVolunteer incVolunteer = IncidentVolunteer.builder().incident(inc).volunteer(user).build();

		incVolunteerRepo.save(incVolunteer);
		inc.setStatus(Incident.Status.IN_PROGRESS);
		incRepo.save(inc);
		return incVolunteer;

	}

	@Override
	public List<Incident> getAllIncident() {
		List<Incident> incidents = incRepo.findAllByOrderByCreatedAtDesc();
		return incidents;
	}

	@Override
	public List<Incident> getIncidentByVolunteer() {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() instanceof User user) {

			List<IncidentVolunteer> volunteers = incVolunteerRepo.findByVolunteerOrderByIncidentCreatedAtDesc(user);
			return volunteers.stream().map(IncidentVolunteer::getIncident).collect(Collectors.toList());

		} else {
			throw new UnauthorizedUserException("User is unauthentical or not valid");
		}
	}

	@Override
	public List<Incident> getIncidentByDate(DateModel model) {
		List<Incident> incidents = incRepo.findByCreatedAtBetween(model.getStartDate().atStartOfDay(),
				model.getEndDate().atTime(LocalTime.MAX));
		return incidents;
	}

	@Override
	public void resolveIncident(Incident inc) {

		if (inc == null) {
			throw new IncidentNotFoundException();
		}

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() instanceof User user) {

			if (isVolunteerRelated(user, inc)) {
				inc.setStatus(Incident.Status.RESOLVED);
				inc.setResolvedAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toLocalDateTime());
				incRepo.save(inc);
				return;
			}

		}
		throw new UnauthorizedUserException("User is unauthentical or not valid");

	}

	private boolean isVolunteerRelated(User user, Incident inc) {
		if (!user.getRole().equals(Role.ROLE_VOLUNTEER))
			return false;
		Optional<IncidentVolunteer> vol = incVolunteerRepo.findByVolunteerAndIncident(user, inc);
		return vol.isPresent();
	}

//	@Override
//	public List<Incident> searchIncident(SearchModel model) {
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		if (auth != null && auth.getPrincipal() instanceof User user) {
//			List<Incident> incidents = null;
//			Incident.Status status = null;
//			if(user.getRole().equals(Role.ROLE_VOLUNTEER)) {
//				
//			}else {
//				LocalDateTime start = model.getStartDate() != null ? model.getStartDate().atStartOfDay() : null;
//				LocalDateTime end = model.getEndDate() != null ? model.getEndDate().atTime(LocalTime.MAX) : null;
//				String keyword = (model.getKeyword() != null && !model.getKeyword().isBlank()) ? model.getKeyword() : null;
//				if(keyword!=null && keyword.toLowerCase().startsWith("active")) status = Incident.Status.ACTIVE;
//				else if(keyword!=null && keyword.toLowerCase().startsWith("resolve")) status = Incident.Status.RESOLVED;
//				System.out.println("Keyword value: " + status);
//				System.out.println("Keyword type: " + (keyword == null ? "null" : keyword.getClass().getName()));
//
////				incidents = incRepo.findFilteredIncidents(
////				    user.getRole().equals(Role.ROLE_CITIZEN)?user : null ,
////				    start,
////				    end,
////				    keyword,
////				    status
////				);
//				
//				incidents = incRepo.findAll(
//					    IncidentSpecification.filter(user.getRole().equals(Role.ROLE_ADMIN)?null:user, user.getRole().equals(Role.ROLE_VOLUNTEER) ? user : null, start, end, keyword, status),
//					    Sort.by(Sort.Direction.DESC, "createdAt")
//					);
//
//
//			}
//			return incidents;
//		} else {
//			throw new UnauthorizedUserException("User is unauthentical or not valid");
//		}
//	}

	@Override
	public List<Incident> searchIncident(LocalDate startDate, LocalDate endDate, String keyword) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() instanceof User user) {
			List<Incident> incidents = null;
			Incident.Status status = null;

			LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
			LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
			keyword = (keyword!=null && keyword.isBlank()) ? null : keyword;
			if (keyword != null && keyword.toLowerCase().contains("active"))
				status = Incident.Status.ACTIVE;
			else if (keyword != null && keyword.toLowerCase().contains("resolve"))
				status = Incident.Status.RESOLVED;
			System.out.println("Keyword value: " + status);
			System.out.println("Keyword type: " + (keyword == null ? "null" : keyword.getClass().getName()));

//				incidents = incRepo.findFilteredIncidents(
//				    user.getRole().equals(Role.ROLE_CITIZEN)?user : null ,
//				    start,
//				    end,
//				    keyword,
//				    status
//				);

			incidents = incRepo.findAll(
					IncidentSpecification.filter(user.getRole().equals(Role.ROLE_CITIZEN) ? user : null,
							user.getRole().equals(Role.ROLE_VOLUNTEER) ? user : null, start, end, keyword),
					Sort.by(Sort.Direction.DESC, "createdAt"));

			return incidents;
		} else {
			throw new UnauthorizedUserException("User is unauthentical or not valid");
		}
	}

}
