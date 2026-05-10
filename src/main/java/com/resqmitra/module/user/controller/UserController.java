package com.resqmitra.module.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.resqmitra.dto.ApiResponse;
import com.resqmitra.module.user.dto.RegisterUserModel;
import com.resqmitra.module.user.dto.UserLocationUpdateModel;
import com.resqmitra.module.user.dto.UserUpdateModel;
import com.resqmitra.module.user.entity.User;
import com.resqmitra.module.user.exception.UserAlreadyCreatedException;
import com.resqmitra.module.user.service.UserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordEncoder passEncoder;
	
	
	@PostMapping("/register")
	public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterUserModel model) throws UserAlreadyCreatedException{
		
		model.setPassword(passEncoder.encode(model.getPassword()));
		
		User user = userService.save(model);
		
		if(user==null) {
			log.warn("User is not created");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			        .body(new ApiResponse(false, null, "User Not Created"));
		
		}
		
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new ApiResponse(true, null, "User is created"));
	}
	
	@DeleteMapping("/delete")
	@PreAuthorize("authenticated()")
	public ResponseEntity<ApiResponse> deleteUser(){
		userService.deleteUser();
		return ResponseEntity.ok(new ApiResponse(true , null , "User deactivated successfully"));
	}
	
	@PutMapping("/update")
	@PreAuthorize("authenticated()")
	public ResponseEntity<ApiResponse> updateUser(@RequestBody @Valid UserUpdateModel model){
		userService.updateUser(model);
		
		return ResponseEntity.ok(new ApiResponse(true, null, "User is updated successfully"));
	}
	
	@PutMapping("/update/location")
	@PreAuthorize("authenticated()")
	public ResponseEntity<ApiResponse> updateLocation(@RequestBody @Valid UserLocationUpdateModel model){
		userService.updateLocation(model);
		return ResponseEntity.ok(new ApiResponse(true , null , "User Location updated"));
	}
	
	@GetMapping("/get/{emailId}")
	public ResponseEntity<ApiResponse> getUserByEmail(@PathVariable String emailId){
		User user = userService.getUserById(emailId);
		if(user==null) {
			throw new UsernameNotFoundException("User with given username not found. : " + emailId);
		}
		
		return ResponseEntity.ok(new ApiResponse(true, user, "User is found"));
	}
	
	
	@GetMapping("/get/volunteer")
	public ResponseEntity<ApiResponse> getAllUser(){
		List<User> users = userService.getAllUser();
	    return ResponseEntity.ok(new ApiResponse(true , users , "List of Volunteers")); // 200 OK

	}
	
	@GetMapping("/get/volunteer/search/{keyword}")
	public ResponseEntity<ApiResponse> searchVolunteer(@PathVariable String keyword){
		List<User> users = userService.searchUser(keyword);
	    return ResponseEntity.ok(new ApiResponse(true , users , "List of Volunteers")); // 200 OK

	}
}
