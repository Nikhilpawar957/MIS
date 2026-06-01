package org.example.mis.dtos;

import lombok.Data;

@Data
public class ResetPasswordDto {
	private String token;
    private String otp;
    private String newPassword;
    private String confirmPassword;
}
