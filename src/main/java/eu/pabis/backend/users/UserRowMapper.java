package eu.pabis.backend.users;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;

import eu.pabis.backend.models.UserModel;

public class UserRowMapper implements RowMapper<UserModel> {

	public static final String ID = "id";
	public static final String USERNAME = "username";
	public static final String EMAIL = "email";
	public static final String PASSWORD = "passwordHash";
	
	@Override
	public UserModel mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new UserModel(rs.getString(ID), rs.getString(USERNAME), rs.getString(EMAIL), rs.getString(PASSWORD));
	}

}
