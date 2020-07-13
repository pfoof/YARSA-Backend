package eu.pabis.backend.services;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import eu.pabis.backend.models.UserModel;
import eu.pabis.backend.users.WrongEmailException;
import eu.pabis.backend.users.WrongPasswordException;
import eu.pabis.backend.users.WrongUsernameException;

@Service
public class UserService {
	
	@Autowired
	DataSource dataSource;
	
	public void registerUser(String email, String username, String password)
			throws WrongPasswordException, WrongUsernameException, WrongEmailException {
		
	}
	
	private NamedParameterJdbcTemplate template;
	
	public UserService() {
		template = new NamedParameterJdbcTemplate(dataSource);
	}
	
	public UserModel findUserById(String id) {
		final String sql = "SELECT * FROM users WHERE id = :id";
		SqlParameterSource params = new MapSqlParameterSource()
				.addValue("id", id);
		return null;
	}
	
}
