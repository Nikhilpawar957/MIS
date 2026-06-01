package org.example.mis.exception;

import org.example.mis.dtos.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(UserAlreadyExistException.class)
	public ResponseEntity<ApiResponse<?>> handleUserAlreadyExist(UserAlreadyExistException ex){
		ApiResponse<?> response = new ApiResponse<>(false, ex.getMessage(), null);
		return new ResponseEntity<>(response, HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ApiResponse<?>> handleUserNotFound(UserNotFoundException ex){
		ApiResponse<?> response = new ApiResponse<>(false, ex.getMessage(), null);
		return new ResponseEntity<>(response, HttpStatus.CONFLICT);
	}
}
