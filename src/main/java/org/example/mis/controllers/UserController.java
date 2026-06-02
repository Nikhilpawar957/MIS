package org.example.mis.controllers;

import java.util.List;

import org.example.mis.dtos.ApiResponse;
import org.example.mis.dtos.UserRequestDto;
import org.example.mis.dtos.UserResponseDto;
import org.example.mis.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/api/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userServ;

	@GetMapping("/all")
	public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllUsers() {
		List<UserResponseDto> list = userServ.getAllUsers();

		if (list.isEmpty()) {
			return ResponseEntity.status(404).body(new ApiResponse<>(false, "No data found", list));
		}

		return ResponseEntity.ok(new ApiResponse<>(true, "Data Found", list));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(@PathVariable Long id) {
		UserResponseDto data = userServ.getUserById(id);

		return ResponseEntity.ok(new ApiResponse<>(true, "Data Found", data));
	}

	@PostMapping("/add")
	public ResponseEntity<ApiResponse<UserResponseDto>> addUser(@RequestBody UserRequestDto userReq) {
		UserResponseDto saved = userServ.addUser(userReq);

		ApiResponse<UserResponseDto> response = new ApiResponse<>(true, "Added successfully", saved);

		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id){
        boolean deleted = userServ.deleteUser(id);

        if(deleted){
            return ResponseEntity.ok(new ApiResponse<>(true,"Deleted successfully", null));
        } else{
            return ResponseEntity.status(404).body(new ApiResponse<>(false, "Something went wrong", null));
        }
    }
}
