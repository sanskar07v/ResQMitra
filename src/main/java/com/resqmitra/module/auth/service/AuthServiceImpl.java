package com.resqmitra.module.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.resqmitra.module.auth.dto.LoginModel;
import com.resqmitra.module.auth.exception.UserIdAndPasswordNotMatchException;
import com.resqmitra.module.user.entity.User;
import com.resqmitra.module.user.repo.UserRepository;
import com.resqmitra.module.user.service.UserService;
import com.resqmitra.utilities.UserStatus;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
	
	@Autowired
	private PasswordEncoder passEncoder;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepo;

	@Override
	public User checkUser(@Valid LoginModel model) throws UserIdAndPasswordNotMatchException {
		
		User user = userService.getUserByIdAndStatus(model.getEmail() , UserStatus.ACTIVE);
		
		if(user == null) {
			log.warn("User not found for email: {}", model.getEmail());
			throw new UsernameNotFoundException("User not found for email: " + model.getEmail());
		}
		
		if(passEncoder.matches(model.getPassword(), user.getPassword())) {
			user.setLongitude(model.getLongitude());
			user.setLatitude(model.getLatitude());
			userRepo.save(user);
			return user;
		}
		
		log.warn("User Id and Password does not match {} " + model.getEmail());
		throw new UserIdAndPasswordNotMatchException("User id and Password does not match");
		
		
	}

}
