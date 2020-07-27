package eu.pabis.backend.services;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public abstract class AbstractSQLService {

	@Autowired
	private NamedParameterJdbcTemplate template;
	
	public void closeConnection() throws SQLException {
		template.getJdbcTemplate().getDataSource().getConnection().close();
	}
	
}
