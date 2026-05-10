package com.resqmitra.module.user.exception;

public class UserAlreadyCreatedException extends RuntimeException{

	private static final long serialVersionUID = -2900041140244138082L;
	
	public UserAlreadyCreatedException(String message) {
		super(message);
	}

}
