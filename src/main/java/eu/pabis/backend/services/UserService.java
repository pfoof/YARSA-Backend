package eu.pabis.backend.services;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import eu.pabis.backend.models.UserModel;
import eu.pabis.backend.users.NoSuchUserException;
import eu.pabis.backend.users.UserRowMapper;
import eu.pabis.backend.users.WrongEmailException;
import eu.pabis.backend.users.WrongPasswordException;
import eu.pabis.backend.users.WrongUsernameException;

@Service
public class UserService {
	
	@Autowired
	DataSource dataSource;
	
	@Autowired
	SessionService sessionService;
	
	public void registerUser(String email, String username, String password)
			throws WrongPasswordException, WrongUsernameException, WrongEmailException {
		
		// Validating password
		checkPassword(password);
		
		UserModel user = new UserModel(username, email, BCrypt.hashpw(password, BCrypt.gensalt()));
		
		Set<ConstraintViolation<UserModel>> violations = Validation.buildDefaultValidatorFactory().getValidator().validate(user);
		if(violations.isEmpty())
			insertUser(user);
		else {
			for(ConstraintViolation<UserModel> violation : violations) {
				if(violation.getPropertyPath().toString().contains("username"))
					throw new WrongUsernameException(violation.getMessage());
				if(violation.getPropertyPath().toString().contains("email"))
					throw new WrongEmailException(violation.getMessage());
			}
		}
		
	}
	
	public String loginUser(String username, String password) 
		throws WrongPasswordException, WrongUsernameException {
		
		UserModel user = findUserByUsername(username);
		
		if(user == null)
			throw new WrongUsernameException("No such user!");
		
		if(user.verifyPassword(password)) {
			return sessionService.createSessionForUser(user.id);
		} else
			throw new WrongPasswordException("Password is wrong!");
	}
	
	private NamedParameterJdbcTemplate template = null;
	
	public static final String SCHEMA = "CREATE TABLE IF NOT EXISTS users (\n" + 
			"    "+UserRowMapper.ID+" varchar(128)  NOT NULL,\n" + 
			"    "+UserRowMapper.USERNAME+" varchar(32)  NOT NULL,\n" + 
			"    "+UserRowMapper.EMAIL+" varchar(200)  NOT NULL,\n" + 
			"    "+UserRowMapper.PASSWORD+" varchar(200) NOT NULL,\n" + 
			"    CONSTRAINT uinque_username UNIQUE ("+UserRowMapper.USERNAME+") NOT DEFERRABLE  INITIALLY IMMEDIATE,\n" + 
			"    CONSTRAINT unique_email UNIQUE ("+UserRowMapper.EMAIL+") NOT DEFERRABLE  INITIALLY IMMEDIATE,\n" + 
			"    CONSTRAINT users_pk PRIMARY KEY ("+UserRowMapper.ID+")\n" + 
			");\n" + 
			"\n" + 
			"CREATE INDEX IF NOT EXISTS ID_Index on users ("+UserRowMapper.ID+" ASC);\n" + 
			"\n" + 
			"CREATE INDEX IF NOT EXISTS username_index on users ("+UserRowMapper.USERNAME+" ASC);";
	
	public UserService() throws SQLException{
		if(dataSource != null) {
			template = new NamedParameterJdbcTemplate(dataSource);
			initDatabase();
		}
	}
	
	public UserService(DataSource dataSource) throws SQLException {
		this.dataSource = dataSource;
		template = new NamedParameterJdbcTemplate(dataSource);
		initDatabase();
	}
	
	private void initDatabase() throws SQLException {
		dataSource.getConnection().createStatement().execute(SCHEMA);
	}
	
	public UserModel findUserById(String id) {
		final String sql = String.format("SELECT * FROM users WHERE %s = :id", UserRowMapper.ID);
		SqlParameterSource params = new MapSqlParameterSource()
				.addValue("id", id);
		List<UserModel> user = template.query(sql, params, new UserRowMapper());  
		return user.isEmpty() ? null : user.get(0);
	}
	
	public UserModel findUserByUsername(String username) {
		final String sql = String.format("SELECT * FROM users WHERE %s = :username", UserRowMapper.USERNAME);
		SqlParameterSource params = new MapSqlParameterSource()
				.addValue("username", username);
		List<UserModel> user = template.query(sql, params, new UserRowMapper());
		return user.isEmpty() ? null : user.get(0);
	}
	
	private String insertUser(UserModel userModel) {
		final String sql = String.format("INSERT INTO users (%s, %s, %s, %s) VALUES (:id, :email, :username, :password)",
				UserRowMapper.ID, UserRowMapper.EMAIL, UserRowMapper.USERNAME, UserRowMapper.PASSWORD);
		SqlParameterSource params = new MapSqlParameterSource()
				.addValue("id", userModel.id)
				.addValue("email", userModel.email)
				.addValue("username", userModel.username)
				.addValue("password", userModel.passwordHash);
		template.update(sql, params);
		
		return userModel.id;
	}
	
	public void updatePassword(String id, String password, String newPassword) throws WrongPasswordException, NoSuchUserException {
		checkPassword(newPassword);
		
		UserModel user = findUserById(id);
		if(user == null) throw new NoSuchUserException("User with this id does not exist!");
		
		if(!user.changePassword(password, newPassword)) 
			throw new WrongPasswordException("Old password is wrong!");
		
		final String sql = String.format("UPDATE users SET %s = :password WHERE %s = :id", UserRowMapper.PASSWORD, UserRowMapper.ID);
		SqlParameterSource params = new MapSqlParameterSource()
				.addValue("id", user.id)
				.addValue("password", user.passwordHash);
		template.update(sql, params);
	}
	
	public void deleteUser(String id) throws NoSuchUserException {
		UserModel user = findUserById(id);
		if(user == null) throw new NoSuchUserException("User with this id does not exist!");
		
		final String sql = String.format("DELETE FROM users WHERE %s = :id", UserRowMapper.ID);
		SqlParameterSource params = new MapSqlParameterSource()
				.addValue("id", user.id);
		template.update(sql, params);
	}
	
	private void checkPassword(String password) throws WrongPasswordException {
		if(password == null || password.length() < 6) throw new WrongPasswordException("Password is too short (6 chars minimum)!");
	}
	
}
