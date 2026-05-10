package com.resqmitra.module.auth.service;

import com.resqmitra.module.auth.dto.LoginModel;
import com.resqmitra.module.auth.exception.UserIdAndPasswordNotMatchException;
import com.resqmitra.module.user.entity.User;

import jakarta.validation.Valid;

public interface AuthService {

	User checkUser(@Valid LoginModel model) throws UserIdAndPasswordNotMatchException;

}
