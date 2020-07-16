package eu.pabis.backend.models;

import java.util.UUID;

import javax.validation.constraints.*;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class UserModel {

	public String id;
	
	@NotNull(message = "Username should not be empty!")
	@Size(min = 3, max = 20, message = "Username should be between 3 and 20 chars.")
	@Pattern(regexp = "[A-Za-z0-9_-]+", message = "Username should only contain alphanumeric characters, '-' and '_'.")
	public String username;
	
	@Email(message = "Email should be a valid address!")
	@NotBlank(message = "Email must not be blank!")
	@NotNull(message = "Email must not be blank!")
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
		if(currentPassword == null || newPassword == null) return false;
		if(verifyPassword(currentPassword)) {
			passwordHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
			return true;
		}
		return false;
	}
	
}
