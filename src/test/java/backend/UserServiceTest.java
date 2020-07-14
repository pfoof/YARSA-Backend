package backend;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import javax.sql.DataSource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import eu.pabis.backend.Main;
import eu.pabis.backend.config.DataSourceConfig;
import eu.pabis.backend.models.UserModel;
import eu.pabis.backend.services.UserService;
import eu.pabis.backend.users.NoSuchUserException;
import eu.pabis.backend.users.WrongEmailException;
import eu.pabis.backend.users.WrongPasswordException;
import eu.pabis.backend.users.WrongUsernameException;

@ContextConfiguration(initializers = { UserServiceTest.Initializer.class } )
@SpringBootTest(classes = { DataSourceConfig.class, DataSource.class, UserModel.class, UserService.class })
public class UserServiceTest {
 
    static class Initializer
      implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        	String dbUrl = System.getenv("DATABASE_URL");
    		URI uri;
			try {
				uri = new URI(dbUrl);
				TestPropertyValues.of(
			              "spring.datasource.url=" + DataSourceConfig.getURL(uri),
			              "spring.datasource.username=" + DataSourceConfig.getUsername(uri),
			              "spring.datasource.password=" + DataSourceConfig.getPassword(uri)
			            ).applyTo(configurableApplicationContext.getEnvironment());
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
        }
    }
	
    @Autowired
	DataSource dataSource;
    
	@Autowired
	UserService service;
	
	UserModel user;
	private final String PROPER_PASSWORD = "passwordTest123";
	private final String PROPER_EMAIL = "useremail1@domain.org";
	private final String PROPER_USERNAME = "useR_123";
	private final String[] BAD_PASSWORDS = { "aaa" , null };
	private final String[] BAD_EMAILS = {"foobar", "", null};
	private final String[] BAD_USERNAMES = { "f", "", null, "!Aaaa!" };
	
	@Test @DisplayName("Testing register validation")
	public void userRegisterValidate() {
		
		for(String badPassword : BAD_PASSWORDS)
			assertThrows(WrongPasswordException.class, () -> {
				service.registerUser(PROPER_EMAIL, PROPER_USERNAME, badPassword);
			});
		
		for(String badEmail : BAD_EMAILS)
			assertThrows(WrongEmailException.class, () -> {
				service.registerUser(badEmail, PROPER_USERNAME, PROPER_PASSWORD);
			});
		
		for(String badUsername : BAD_USERNAMES)
			assertThrows(WrongUsernameException.class, () -> {
				service.registerUser(PROPER_EMAIL, badUsername, PROPER_PASSWORD);
			});
		
		assertDoesNotThrow(() -> {
			service.registerUser(PROPER_EMAIL, PROPER_USERNAME, PROPER_PASSWORD);
		});
	}
	
	@Test @DisplayName("Testing user search by username")
	public void userFindUsername() {
		user = service.findUserByUsername(PROPER_USERNAME);
		assertNotNull(user);
		
		assertTrue(user.username.equals(PROPER_USERNAME));
		assertTrue(user.email.equals(PROPER_EMAIL));
		assertTrue(user.verifyPassword(PROPER_PASSWORD));
		
		UserModel nonExistent = service.findUserByUsername("helloworld");
		assertNull(nonExistent);
	}
	
	@Test @DisplayName("Testing user search by id")
	public void userFindId() {
		UserModel user2 = service.findUserById(user.id);
		
		assertNotNull(user2);
		assertTrue(user.username.equals(user2.username));
		
		user2 = service.findUserById(UUID.randomUUID().toString());
		assertNull(user2);
	}
	
	@Test @DisplayName("Testing user password change")
	public void userChangePassword() {
		assertThrows(NoSuchUserException.class, () -> {
			service.updatePassword(UUID.randomUUID().toString(), PROPER_PASSWORD, PROPER_PASSWORD);
		});
		
		for(String badPassword: BAD_PASSWORDS)
			assertThrows(WrongPasswordException.class, () -> {
				service.updatePassword(user.id, badPassword, PROPER_PASSWORD);
			});
		
		for(String badPassword : BAD_PASSWORDS)
			assertThrows(WrongPasswordException.class, () -> {
				service.updatePassword(user.id, PROPER_PASSWORD, badPassword);
			});
		
		assertDoesNotThrow(() -> {
			service.updatePassword(user.id, PROPER_PASSWORD, PROPER_PASSWORD + "1");
		});
	}
	
	@Test @DisplayName("Testing user deletion")
	public void userDelete() {
		assertThrows(NoSuchUserException.class, () -> {
			service.deleteUser(UUID.randomUUID().toString());
		});
		
		assertDoesNotThrow(() -> {
			service.deleteUser(user.id);
		});
		
		assertNull(service.findUserById(user.id));
	}
}
