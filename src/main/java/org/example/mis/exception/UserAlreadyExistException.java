package org.example.mis.exception;

@SuppressWarnings("serial")
public class UserAlreadyExistException extends RuntimeException {
	public UserAlreadyExistException(String message) {
		super(message);
	}
}
