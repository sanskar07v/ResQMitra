package com.resqmitra.module.user.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.resqmitra.module.user.entity.User;
import com.resqmitra.utilities.Role;
import com.resqmitra.utilities.UserStatus;

@Repository
public interface UserRepository extends JpaRepository<User, String> , JpaSpecificationExecutor<User> {

	Optional<User> findByEmailAndRole(String volunteerId, Role role);

	@Query(value = """
		    SELECT * FROM users
		    WHERE role = 'ROLE_VOLUNTEER'
		    AND status = 'ACTIVE' /* <--- USE THE ENUM NAME HERE */
		    AND (6371 * acos(
		        cos(radians(:lat)) * cos(radians(latitude)) *
		        cos(radians(longitude) - radians(:lng)) +
		        sin(radians(:lat)) * sin(radians(latitude))
		    )) <= :radius
		    """, nativeQuery = true)
		List<User> findNearbyVolunteers(@Param("lat") double lat, @Param("lng") double lng,
		        @Param("radius") double radiusKm);

	Optional<User> findByEmailAndStatus(String email, UserStatus status);

	List<User> findByRole(Role role);
	
	List<User> findByRoleAndNameContainingIgnoreCaseOrRoleAndEmailContainingIgnoreCase(
	        Role role1, String nameKeyword,
	        Role role2, String emailKeyword
	    );

}
