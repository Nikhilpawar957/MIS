package org.example.mis.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

	@Value("${app.secret-key}")
	private String SECRET_KEY;

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public <T> T extractClaim(String token, Function<Claims, T> resolver) {
		final Claims claims = extractAllClaims(token);
		if (claims == null) return null;
		return resolver.apply(claims);
	}

	public String generateToken(UserDetails userDetails, String type) {
		long now = System.currentTimeMillis();
	    long expirationMs = 1000L * 60 * 60 * 24 * 7; // 7 days
	    
	    Map<String, Object> claims = new HashMap<>();
	    claims.put("type", type); // "ADMIN" or "CUSTOMER"

	    return Jwts.builder()
	            .setClaims(claims)
	            .setSubject(userDetails.getUsername())
	            .setIssuedAt(new Date(now))
	            .setExpiration(new Date(now + expirationMs))
	            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
	            .compact();
	}

	public String generateRefreshToken(UserDetails userDetails, String userType) {
	    long now = System.currentTimeMillis();
	    long expirationMs = 1000L * 60 * 60 * 24 * 30; // 30 days

	    Map<String, Object> claims = new HashMap<>();
	    claims.put("type", userType); // Store the type here too!

	    return Jwts.builder()
	            .setClaims(claims)
	            .setSubject(userDetails.getUsername())
	            .setIssuedAt(new Date(now))
	            .setExpiration(new Date(now + expirationMs))
	            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
	            .compact();
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);

		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	private boolean isTokenExpired(String token) {
		return extractClaim(token, Claims::getExpiration).before(new Date());
	}

	private Claims extractAllClaims(String token) {
		try {
	        return Jwts.parserBuilder()
	                .setSigningKey(getSignInKey())
	                .build()
	                .parseClaimsJws(token)
	                .getBody();
	    } catch (Exception e) {
	        return null;
	    }
	}

	private Key getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);

		return Keys.hmacShaKeyFor(keyBytes);
	}
}
