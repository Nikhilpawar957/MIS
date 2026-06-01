package org.example.mis.services;

import org.example.mis.dtos.LoginRequestDto;
import org.example.mis.dtos.LoginResponseDto;
import org.example.mis.dtos.RefreshTokenResponseDto;
import org.example.mis.dtos.ResetPasswordDto;

public interface AuthService {
	LoginResponseDto login(LoginRequestDto request);
	
	String forgotPassword(String email);
	
	String resetPassword(ResetPasswordDto request);
	
	RefreshTokenResponseDto refreshToken(String refreshToken);
}
