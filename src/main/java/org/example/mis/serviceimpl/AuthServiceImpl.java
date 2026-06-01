package org.example.mis.serviceimpl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.example.mis.config.JwtService;
import org.example.mis.config.UserDetailServiceImpl;
import org.example.mis.dtos.LoginRequestDto;
import org.example.mis.dtos.LoginResponseDto;
import org.example.mis.dtos.RefreshTokenResponseDto;
import org.example.mis.dtos.ResetPasswordDto;
import org.example.mis.dtos.UserResponseDto;
import org.example.mis.entities.User;
import org.example.mis.exception.PasswordMismatchException;
import org.example.mis.exception.UserNotFoundException;
import org.example.mis.repositories.UserRepo;
import org.example.mis.services.AuthService;
import org.example.mis.utils.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.constraints.NotNull;

@Service
@Validated
public class AuthServiceImpl implements AuthService {

	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	JwtService jwtService;
	
	@Autowired
	UserRepo userRepo;
	
	@Autowired
	UserDetailServiceImpl userDetailServ;
	
	@Autowired
	UserServiceImpl userServ;
	
	@Autowired
	EmailUtil emailUtil;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Value("${app.frontend.url}")
	private String frontendUrl;
	
	@Override
	public LoginResponseDto login(LoginRequestDto request) {
		try {
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
			
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			
			User user = userRepo.findByEmail(userDetails.getUsername()).orElseThrow(() -> new UserNotFoundException("User Not Found"));
			
			String token = jwtService.generateToken(userDetails, user.getRole().toString());
			String refreshToken = jwtService.generateRefreshToken(userDetails, user.getRole().toString());
			
			UserResponseDto userDto = userServ.mapToDto(user);
			
			return new LoginResponseDto(userDto, token, refreshToken);
			
		} catch (BadCredentialsException ex) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
		} catch (UsernameNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
		} catch (AuthenticationException ex) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
		} catch (Exception ex) {
			// Debug: Print the actual exception to identify unexpected errors
			System.err.println("Login exception: " + ex.getClass().getName() + " - " + ex.getMessage());
			ex.printStackTrace();
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Authentication failed");
		}
	}

	@Override
	public String forgotPassword(@NotNull String email) {
		Optional<User> optionalUser = userRepo.findByEmail(email);
		
		if(optionalUser.isEmpty()) {
			throw new UserNotFoundException("User Not Found");
		}
		
		User user = optionalUser.get();
		
		String otp = String.format("%06d", new Random().nextInt(999999));
		String token = UUID.randomUUID().toString();

		user.setResetOtp(otp);
		user.setResetToken(token);
		user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
		
		userRepo.save(user);
		
		String resetLink = frontendUrl + "/reset-password?token=" + token;
		
		Map<String, Object> variables = new HashMap<>();

		variables.put("customerName", user.getFullName());
		variables.put("otp", otp);
		variables.put("resetLink", resetLink);

		emailUtil.sendHtmlMail(user.getEmail(), "Reset Your Password", "email/forgot-password", variables);
		
		return "Reset Email Sent";
	}

	@Override
	public String resetPassword(ResetPasswordDto request) {
		Optional<User> optionalUser = userRepo.findByResetToken(request.getToken());
		
		if(optionalUser.isEmpty()) {
			throw new UserNotFoundException("User Not Found");
		}
		
		User user = optionalUser.get();
		
		if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("Token Expired");
		}

		if (!user.getResetOtp().equals(request.getOtp())) {
			throw new RuntimeException("Invalid OTP");
		}

		if (!request.getNewPassword().equals(request.getConfirmPassword())) {
			throw new PasswordMismatchException("Passwords Do Not Match");
		}
		
		user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));

		user.setResetOtp(null);
		user.setResetToken(null);
		user.setResetTokenExpiry(null);

		userRepo.save(user);

		return "Password reset successful";
	}

	@Override
	public RefreshTokenResponseDto refreshToken(String refreshToken) {
		String username = jwtService.extractUsername(refreshToken);
		
		UserDetails userDetails = userDetailServ.loadUserByUsername(username);
		
		if (!jwtService.isTokenValid(refreshToken, userDetails)) {
			throw new RuntimeException("Invalid Token or Token Expired");
		}
		
		User user = userRepo.findByEmail(userDetails.getUsername()).orElseThrow(() -> new UserNotFoundException("User Not Found"));

		String newAccessToken = jwtService.generateToken(userDetails, user.getRole().toString());

		return new RefreshTokenResponseDto(newAccessToken, refreshToken);
	}

}
