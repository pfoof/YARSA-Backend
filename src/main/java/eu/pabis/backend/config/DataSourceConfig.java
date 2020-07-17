package eu.pabis.backend.config;

import java.net.URI;
import java.net.URISyntaxException;
import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration
public class DataSourceConfig {

	@Bean
	public DataSource getDataSource() throws URISyntaxException {
		DataSourceBuilder builder = DataSourceBuilder.create();
		String dbUrl = System.getenv("DATABASE_URL");
		URI uri = new URI(dbUrl);
		builder.url(getURL(uri));
		builder.username(getUsername(uri));
		builder.password(getPassword(uri));
		builder.driverClassName( DatabaseDriver.POSTGRESQL.getDriverClassName() );
	
		return builder.build();
	}
	
	@Bean
	public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() throws URISyntaxException {
		return new NamedParameterJdbcTemplate(getDataSource());
	}
	
	public static String getUsername(URI uri) {
		return uri.getUserInfo().split(":")[0];
	}
	
	public static String getPassword(URI uri) {
		return uri.getUserInfo().split(":")[1];
	}
	
	public static String getURL(URI uri) {
		return "jdbc:" + uri.getScheme().replaceAll("postgres", "postgresql") + "://" + uri.getHost() + ":" + uri.getPort() + uri.getPath();
	}
	
}
