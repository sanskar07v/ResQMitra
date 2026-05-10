package com.resqmitra.module.incident.specification;

import com.resqmitra.module.incident.entity.Incident;
import com.resqmitra.module.user.entity.User;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

@Slf4j
public class IncidentSpecification {

    @SuppressWarnings("removal")
	public static Specification<Incident> filter(
    		User user ,
    		User volunteer,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String keyword
    ) {
        return Specification
        		.where(createdBy(user))
        		.and(volunteer(volunteer))
                .and(startDate(startDate))
                .and(endDate(endDate))
                .and(keyword(keyword));
      }

    private static Specification<Incident> createdBy(User createdBy) {
        return (root, query, cb) -> {
            if (createdBy == null) return null;
            return cb.equal(root.get("createdBy"), createdBy);
        };
    }

    public static Specification<Incident> volunteer(User volunteer) {
        return (root, query, cb) -> {
            if (volunteer == null) return cb.conjunction();
            var join = root.join("volunteers");
            return cb.equal(join.get("volunteer"), volunteer);
        };
    }

    private static Specification<Incident> startDate(LocalDateTime startDate) {
        return (root, query, cb) -> {
            if (startDate == null) return null;
            return cb.greaterThanOrEqualTo(root.get("createdAt"), startDate);
        };
    }

    private static Specification<Incident> endDate(LocalDateTime endDate) {
        return (root, query, cb) -> {
            if (endDate == null) return null;
            return cb.lessThanOrEqualTo(root.get("createdAt"), endDate);
        };
    }

    private static Specification<Incident> keyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;

            String key = "%" + keyword.toLowerCase() + "%";

            // description LIKE %keyword%
            var descMatch = cb.like(cb.lower(root.get("description")), key);

            // If keyword contains "active", include OR status = ACTIVE
            if (keyword.toLowerCase().contains("active")) {
                var statusMatch = cb.equal(root.get("status"), Incident.Status.ACTIVE);
                return cb.or(descMatch, statusMatch);
            }

            // If keyword contains "resolve", include OR status = RESOLVED
            if (keyword.toLowerCase().contains("resolve")) {
                var statusMatch = cb.equal(root.get("status"), Incident.Status.RESOLVED);
                return cb.or(descMatch, statusMatch);
            }
            
         // If keyword contains "progress", include OR status = IN_PROGRESS
            if (keyword.toLowerCase().contains("progress")) {
                var statusMatch = cb.equal(root.get("status"), Incident.Status.IN_PROGRESS);
                return cb.or(descMatch, statusMatch);
            }

            return descMatch;
        };
    }

}
