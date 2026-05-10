package com.resqmitra.module.incident.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.resqmitra.module.incident.entity.Incident;
import com.resqmitra.module.incident.entity.IncidentVolunteer;
import com.resqmitra.module.user.entity.User;

public interface IncidentVolunteerRepo extends JpaRepository<IncidentVolunteer, Long> {

	List<IncidentVolunteer> findByVolunteer(User user);

	Optional<IncidentVolunteer> findByVolunteerAndIncident(User user, Incident inc);

	List<IncidentVolunteer> findByVolunteerOrderByIncidentCreatedAtDesc(User user);

	List<IncidentVolunteer> findByIncident(Incident inc);


}
