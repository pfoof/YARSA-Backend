package eu.pabis.backend.config;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.sql.DataSource;

import org.postgresql.Driver;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

	@Bean
	public DataSource getDataSource() throws URISyntaxException {
		DataSourceBuilder builder = DataSourceBuilder.create();
		String dbUrl = System.getenv("DATABASE_URL").replaceAll("postgres", "postgresql");
		URI uri = new URI(dbUrl);
		String user_password = uri.getUserInfo();
		builder.url("jdbc:" + uri.getScheme() + "://" + uri.getHost() + ":" + uri.getPort() + uri.getPath());
		builder.username(user_password.split(":")[0]);
		builder.password(user_password.split(":")[1]);
		builder.driverClassName( DatabaseDriver.POSTGRESQL.getDriverClassName() );
		return builder.build();
	}
	
}
