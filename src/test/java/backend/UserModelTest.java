package backend;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCrypt;

import eu.pabis.backend.models.UserModel;

@SpringBootTest(classes = UserModel.class)
public class UserModelTest {
	
	@Test @DisplayName("Testing user creation")
	void userCreationTest() {
		String username = "user_username";
		String email = "user@domain.net";
		String password = "password123";
		String passwordHash = BCrypt.hashpw("password123", BCrypt.gensalt());
		
		UserModel user = new UserModel(username, email, passwordHash);
		
		assertTrue(user.email.equalsIgnoreCase(email));
		assertTrue(user.username.equalsIgnoreCase(username));
		assertTrue(user.verifyPassword(password));
		assertFalse(user.verifyPassword("fakepassword"));
	}
	
	@Test @DisplayName("Testing password change")
	void passwordChangeTest() {
		String username = "user_username";
		String email = "user@domain.net";
		String password = "password123";
		String passwordHash = BCrypt.hashpw("password123", BCrypt.gensalt());
		
		UserModel user = new UserModel(username, email, passwordHash);
		
		String newPassword = "1Password!!!";
		
		assertTrue(user.verifyPassword(password));
		assertFalse(user.verifyPassword(newPassword));
		
		assertFalse(user.changePassword(newPassword, newPassword));
		assertTrue(user.changePassword(password, newPassword));
		
		assertFalse(user.verifyPassword(password));
		assertTrue(user.verifyPassword(newPassword));
	}
	
}
