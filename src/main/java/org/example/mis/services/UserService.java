package org.example.mis.services;

import java.util.List;

import org.example.mis.dtos.UserRequestDto;
import org.example.mis.dtos.UserResponseDto;

public interface UserService {
	List<UserResponseDto> getAllUsers();
	
	UserResponseDto getUserById(Long id);
	
	UserResponseDto addUser(UserRequestDto userReq);
	
	boolean deleteUser(Long id);
}
