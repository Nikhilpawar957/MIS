package org.example.mis.entities;
import com.fasterxml.jackson.annotation.JsonCreator;
public enum UserRole {
	ADMIN,
	SALES,
	ACCOUNTS,
	MANAGER;
	
	@JsonCreator
    public static UserRole fromValue(String value) {
        return UserRole.valueOf(value.toUpperCase());
    }
}
