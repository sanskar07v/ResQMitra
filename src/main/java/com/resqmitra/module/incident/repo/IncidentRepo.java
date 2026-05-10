package com.resqmitra.module.incident.repo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.resqmitra.module.incident.entity.Incident;
import com.resqmitra.module.incident.entity.Incident.Status;
import com.resqmitra.module.user.entity.User;

public interface IncidentRepo extends JpaRepository<Incident, Long> , JpaSpecificationExecutor<Incident>{

	List<Incident> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);


	List<Incident> findAllByOrderByCreatedAtDesc();


	List<Incident> findByCreatedByAndCreatedAtBetween(User user, LocalDateTime now, LocalDateTime now2);


	Optional<Incident> findTopByCreatedByAndCreatedAtBetweenOrderByCreatedAtDesc(User user,
			LocalDateTime twentyMinutesAgo, LocalDateTime now);


	List<Incident> findByCreatedAtBetweenAndDescriptionContainingIgnoreCaseOrStatus(
			LocalDateTime atStartOfDay, LocalDateTime atTime, String keyword, Status status);
	
	
	


}
