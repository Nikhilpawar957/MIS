package org.example.mis.controllers;

import org.example.mis.dtos.ApiResponse;
import org.example.mis.dtos.LoginRequestDto;
import org.example.mis.dtos.LoginResponseDto;
import org.example.mis.dtos.RefreshTokenResponseDto;
import org.example.mis.dtos.ResetPasswordDto;
import org.example.mis.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/api/user/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto request) {
		return ResponseEntity.ok(new ApiResponse<>(true, "Logged In", authService.login(request)));
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody String email) {
		return ResponseEntity.ok(new ApiResponse<>(true, authService.forgotPassword(email), null));
	}
	
	@PostMapping("/reset-password")
	public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody ResetPasswordDto request){
		return ResponseEntity.ok(new ApiResponse<>(true, authService.resetPassword(request), null));
	}
	
	@PostMapping("/refresh-token")
	public ResponseEntity<ApiResponse<RefreshTokenResponseDto>> refreshToken(@RequestBody String refreshToken) {
		
		return ResponseEntity.ok(new ApiResponse<>(true, "Refresh Token Updated",
				authService.refreshToken(refreshToken)));
	}
}
