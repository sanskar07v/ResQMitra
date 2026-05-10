package com.resqmitra.module.user.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.resqmitra.utilities.Role;
import com.resqmitra.utilities.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User implements UserDetails {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7412357567197543749L;

	@Id
	private String email;
	
	@JsonIgnore
	@Column(name="password" , nullable = false)
	private String password;
	
	@Column(name = "name" , nullable = false)
	private String name;
	
	@Pattern(
	        regexp = "^(\\+91)?[6-9]\\d{9}$",
	        message = "Invalid phone number. Must be a valid 10-digit number, with optional +91."
	    )
	@Column(name="phone" , nullable=false)
	private String phone;
	
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private Role role = Role.ROLE_CITIZEN;
	
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private UserStatus status = UserStatus.ACTIVE;
	
	private Double latitude;
	
	private Double longitude;
	
	@UpdateTimestamp
	@Builder.Default
	@JsonIgnore
	private LocalDateTime updatedAt = LocalDateTime.now();

	@Override
	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
	    return List.of(new SimpleGrantedAuthority(this.role.name()));
	}


	@Override
	@JsonIgnore
	public String getUsername() {
		return this.email;
	}
	
	@JsonIgnore
	@Override
	public boolean isAccountNonExpired() {
		return UserDetails.super.isAccountNonExpired();
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		return UserDetails.super.isAccountNonLocked();
	}

	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() {
		return UserDetails.super.isCredentialsNonExpired();
	}

	@JsonIgnore
	@Override
	public boolean isEnabled() {
		return this.status == UserStatus.ACTIVE;
	}
}
