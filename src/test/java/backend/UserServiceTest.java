package backend;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.UUID;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import eu.pabis.backend.Main;
import eu.pabis.backend.config.DataSourceConfig;
import eu.pabis.backend.exceptions.AlreadyExistsException;
import eu.pabis.backend.exceptions.NoSuchUserException;
import eu.pabis.backend.exceptions.WrongEmailException;
import eu.pabis.backend.exceptions.WrongPasswordException;
import eu.pabis.backend.exceptions.WrongUsernameException;
import eu.pabis.backend.models.UserModel;
import eu.pabis.backend.services.SessionService;
import eu.pabis.backend.services.UserService;

@ContextConfiguration(initializers = { UserServiceTest.Initializer.class } )
@SpringBootTest(classes = { DataSourceConfig.class, DataSource.class, UserModel.class, UserService.class, SessionService.class })
@TestMethodOrder(OrderAnnotation.class)
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
	
	public static DataSource dataSource;
    
	public static UserService service;
	
	@BeforeAll
	public static void prepareService() throws URISyntaxException, SQLException {
		dataSource = new DataSourceConfig().getDataSource();
		service = new UserService(dataSource);
		//ReflectionTestUtils.setField(UserService.class, "dataSource", dataSource);
	}
	
	UserModel user;
	private final String PROPER_PASSWORD = "passwordTest123";
	private final String PROPER_EMAIL = "useremail1@domain.org";
	private final String PROPER_USERNAME = "useR_123";
	private final String[] BAD_PASSWORDS = { "aaa" , null };
	private final String[] BAD_EMAILS = {"foobar", "", null};
	private final String[] BAD_USERNAMES = { "f", "", null, "!Aaaa!" };
	
	@Test @DisplayName("Testing register validation") @Order(1)
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
		
		assertThrows(AlreadyExistsException.class, () -> {
			service.registerUser(PROPER_EMAIL, PROPER_USERNAME, PROPER_PASSWORD);
		});
	}
	
	@Test @DisplayName("Testing user search by username") @Order(2)
	public void userFindUsername() {
		user = service.findUserByUsername(PROPER_USERNAME);
		assertNotNull(user);
		
		assertTrue(user.username.equals(PROPER_USERNAME));
		assertTrue(user.email.equals(PROPER_EMAIL));
		assertTrue(user.verifyPassword(PROPER_PASSWORD));
		Test_id = user.id;
		Test_username = user.username;
		
		UserModel nonExistent = service.findUserByUsername("helloworld");
		assertNull(nonExistent);
	}
	
	public static String Test_id;
	public static String Test_username;
	
	@Test @DisplayName("Testing user search by id") @Order(3)
	public void userFindId() {
		UserModel user2 = service.findUserById(Test_id);
		
		assertNotNull(user2);
		assertTrue(Test_username.equals(user2.username));
		
		user2 = service.findUserById(UUID.randomUUID().toString());
		assertNull(user2);
	}
	
	@Test @DisplayName("Testing user password change") @Order(4)
	public void userChangePassword() {
		assertThrows(NoSuchUserException.class, () -> {
			service.updatePassword(UUID.randomUUID().toString(), PROPER_PASSWORD, PROPER_PASSWORD);
		});
		
		for(String badPassword: BAD_PASSWORDS)
			assertThrows(WrongPasswordException.class, () -> {
				service.updatePassword(Test_id, badPassword, PROPER_PASSWORD);
			});
		
		for(String badPassword : BAD_PASSWORDS)
			assertThrows(WrongPasswordException.class, () -> {
				service.updatePassword(Test_id, PROPER_PASSWORD, badPassword);
			});
		
		assertDoesNotThrow(() -> {
			service.updatePassword(Test_id, PROPER_PASSWORD, PROPER_PASSWORD + "1");
		});
	}
	
	@Test @DisplayName("Testing user deletion") @Order(5)
	public void userDelete() {
		assertThrows(NoSuchUserException.class, () -> {
			service.deleteUser(UUID.randomUUID().toString());
		});
		
		assertDoesNotThrow(() -> {
			service.deleteUser(Test_id);
		});
		
		assertNull(service.findUserById(Test_id));
	}
}
