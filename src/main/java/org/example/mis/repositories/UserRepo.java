package org.example.mis.repositories;

import java.util.Optional;

import org.example.mis.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long>{
	Optional<User> findByEmail(String email);
	
	Optional<User> findByResetToken(String resetToken);
}
