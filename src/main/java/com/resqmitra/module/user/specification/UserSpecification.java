package com.resqmitra.module.user.specification;

import com.resqmitra.module.user.entity.User;
import com.resqmitra.utilities.Role;
import com.resqmitra.utilities.UserStatus;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.jpa.domain.Specification;


@Slf4j
public class UserSpecification {

    @SuppressWarnings("removal")
	public static Specification<User> filter(String keyword) {
        return Specification.where(keyword(keyword)).and(status(UserStatus.ACTIVE));
    }

    private static Specification<User> keyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;

            String key = "%" + keyword.toLowerCase() + "%";

            // Normal keyword match: name OR email
            var baseMatch = cb.or(
                cb.like(cb.lower(root.get("name")), key),
                cb.like(cb.lower(root.get("email")), key)
            );

            // Special case: keyword mentions "citizen"
            if (keyword.toLowerCase().contains("citizen")) {
                var roleMatch = cb.equal(root.get("role"), Role.ROLE_CITIZEN);
                return cb.or(baseMatch, roleMatch);
            }

            // Special case: keyword mentions "volunteer"
            if (keyword.toLowerCase().contains("volunteer")) {
                var roleMatch = cb.equal(root.get("role"), Role.ROLE_VOLUNTEER);
                return cb.or(baseMatch, roleMatch);
            }

            return baseMatch;
        };
    }
    
    private static Specification<User> status(UserStatus status) {
        return (root, query, cb) -> {
            if(status == null) return null;
            return cb.equal(root.get("status"), status);
        };
    }
}
