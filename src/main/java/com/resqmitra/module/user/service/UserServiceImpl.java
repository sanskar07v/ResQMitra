package com.resqmitra.module.user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.resqmitra.module.auth.exception.UnauthorizedUserException;
import com.resqmitra.module.incident.entity.Incident;
import com.resqmitra.module.incident.specification.IncidentSpecification;
import com.resqmitra.module.user.dto.RegisterUserModel;
import com.resqmitra.module.user.dto.UserLocationUpdateModel;
import com.resqmitra.module.user.dto.UserUpdateModel;
import com.resqmitra.module.user.entity.User;
import com.resqmitra.module.user.exception.UserAlreadyCreatedException;
import com.resqmitra.module.user.repo.UserRepository;
import com.resqmitra.module.user.specification.UserSpecification;
import com.resqmitra.utilities.Role;
import com.resqmitra.utilities.UserStatus;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserDetailsService , UserService {
	
	@Autowired
	private UserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = this.getUserById(username);
		if(user!=null)
			return user;
		throw new UsernameNotFoundException("User with following email is not found : " + username);
	}
	
	@Override
	public User getUserById(String email) {
		Optional<User> userOp = userRepo.findById(email);
		if(userOp.isEmpty())
			return null;
		else
			return userOp.get();
	}

	@Override
	public User save(@Valid RegisterUserModel model) throws UserAlreadyCreatedException {
		
		User userTemp = this.getUserById(model.getEmail());
		
		if(userTemp!=null) {
			log.warn("User with given email id is already registered : {}" , model.getEmail());
			throw new UserAlreadyCreatedException("User is already created");
		}
		
		User user = User.builder()
				.email(model.getEmail())
				.name(model.getName())
				.password(model.getPassword())
				.phone(model.getPhoneNum())
				.role(model.getRole())
				.latitude(model.getLatitude())
				.longitude(model.getLongitude())
				.build();
		userRepo.save(user);
		return user;
	}

	@Override
	public User getUserByIdAndRole(String volunteerId, Role role) {
		Optional<User> userOp = userRepo.findByEmailAndRole(volunteerId , role);
		if(userOp.isEmpty())
			return null;
		else
			return userOp.get();
	}

	@Override
	public List<User> getNearByVolunteer(Incident incident) {
		List<User> users = userRepo.findNearbyVolunteers(incident.getLatitude(), incident.getLongitude(), 2);
		
		if(users==null || users.isEmpty())
			users = userRepo.findNearbyVolunteers(incident.getLatitude(), incident.getLongitude(), 5);
		
		return users;
	}

	@Override
	public void deleteUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() instanceof User user) {
			
			user.setStatus(UserStatus.INACTIVE);
			userRepo.save(user);
			return;
			
		} else {
			throw new UnauthorizedUserException("User is unauthentical or not valid");
		}
	}

	@Override
	public User updateUser(@Valid UserUpdateModel model) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() instanceof User user) {
			
			if(model.getName()!=null)
				user.setName(model.getName());
			if(model.getPhone()!=null)
				user.setPhone(model.getPhone());
			
			userRepo.save(user);
			
			return user;
			
			
		} else {
			throw new UnauthorizedUserException("User is unauthentical or not valid");
		}
		
	}

	@Override
	public User getUserByIdAndStatus(String email, UserStatus status) {
		Optional<User> userOp = userRepo.findByEmailAndStatus(email , status);
		if(userOp.isEmpty()) 
			return null;
		return userOp.get();
	}

	@Override
	public void updateLocation(@Valid UserLocationUpdateModel model) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() instanceof User user) {
			
			try {
			
			user.setLatitude(model.getLatitude());
			user.setLongitude(model.getLongitude());
			
			userRepo.save(user);
			
			}catch(RuntimeException ex) {
				throw ex;
			}
			
			
		} else {
			throw new UnauthorizedUserException("User is unauthentical or not valid");
		}
	}

	@Override
	public List<User> getAllVolunteer() {
		List<User> users = userRepo.findByRole(Role.ROLE_VOLUNTEER);
		return users;
	}

	@Override
	public List<User> searchUser(String keyword) {
		List<User> users = userRepo.findAll(
				UserSpecification.filter(keyword));
		return users;
	}

	@Override
	public List<User> getAllUser() {
		List<User> users =userRepo.findAll();
		return users;
	}

	@Override
	public List<User> searchVolunteer(String keyword) {
		List<User> users = userRepo.findByRoleAndNameContainingIgnoreCaseOrRoleAndEmailContainingIgnoreCase( Role.ROLE_VOLUNTEER,keyword,  Role.ROLE_VOLUNTEER , keyword);
		return users;
	}
}
