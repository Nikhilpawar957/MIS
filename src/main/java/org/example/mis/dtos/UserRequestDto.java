package org.example.mis.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {

    @NotBlank
    @Size(min = 2, max = 50, message = "name must be between 2 and 50 characters")
    @Pattern(regexp = "^[\\p{L}.'\\- ]+$", message = "name contains invalid characters")
    private String fullName;

    @NotBlank(message = "email is required")
    @Email(message = "email is invalid")
    private String email;
    
    @NotBlank(message = "role is required")
    private String role;

    @NotBlank(message = "password is required")
    @Size(min = 6, max = 16, message = "password must be least 6 characters and at most 16 characters long")
    private String password;

    @NotBlank(message = "confirm password is required")
    @Size(min = 6, max = 16, message = "confirm password must be least 6 characters and at most 16 characters long")
    private String confirmPassword;
}
