package eu.pabis.backend.models;

import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserModel {

	public String id;
	
	public String username;
	public String email;
	public String passwordHash; // As Blowfish
	
	public UserModel() {
		id = UUID.randomUUID().toString();
	}
	
	public UserModel(String username, String email, String passwordHash) {
		this();
		this.username = username;
		this.email = email;
		this.passwordHash = passwordHash;
	}
	
	public UserModel(String id, String username, String email, String passwordHash) {
		this(username, email, passwordHash);
		this.id = id;
	}
	
	public boolean verifyPassword(String password) {
		return BCrypt.checkpw(password, passwordHash);
	}
	
	public boolean changePassword(String currentPassword, String newPassword) {
		if(verifyPassword(currentPassword)) {
			passwordHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
			return true;
		}
		return false;
	}
	
}
