package org.example.mis.serviceimpl;

import java.util.List;

import org.example.mis.dtos.UserRequestDto;
import org.example.mis.dtos.UserResponseDto;
import org.example.mis.entities.User;
import org.example.mis.entities.UserRole;
import org.example.mis.exception.PasswordMismatchException;
import org.example.mis.exception.UserNotFoundException;
import org.example.mis.repositories.UserRepo;
import org.example.mis.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
	UserRepo userRepo;
	
	@Autowired
	PasswordEncoder passwordEncoder;

	@Override
	public List<UserResponseDto> getAllUsers() {
		return userRepo.findAll().stream().map(this::mapToDto).toList();
	}

	@Override
	public UserResponseDto getUserById(Long id) {
		User user = userRepo.findById(id).orElseThrow(()->new UserNotFoundException("User not found"));
        return mapToDto(user);
	}

	@Override
	public UserResponseDto addUser(UserRequestDto userReq) {
		if (!userReq.getPassword().equals(userReq.getConfirmPassword())) {
            throw new PasswordMismatchException("Passwords do not match");
        }

        User user = mapToEntity(userReq);
        user.setPasswordHash(passwordEncoder.encode(userReq.getPassword()));
        return mapToDto(userRepo.save(user));
	}

	@Override
	public boolean deleteUser(Long id) {
		User u = userRepo.findById(id).orElse(null);

        if(u != null){
            userRepo.delete(u);
            return true;
        }else{
            return false;
        }
	}

	public UserResponseDto mapToDto(User user) {
		UserResponseDto resDto = new UserResponseDto();
		resDto.setFullName(user.getFullName());
		resDto.setEmail(user.getEmail());
		resDto.setRole(user.getRole().toString());
		return resDto;
	}
	
	private User mapToEntity(UserRequestDto userReq) {
		User user = new User();
		user.setFullName(userReq.getFullName());
		user.setEmail(userReq.getEmail());
		user.setRole(UserRole.valueOf(userReq.getRole().toUpperCase()));
		return user;
	}
}
